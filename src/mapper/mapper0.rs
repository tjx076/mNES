
use crate::cartridge::Cartridge;
use super::IMapper;

pub struct Mapper0<'a> {

    cart: &'a mut Cartridge,

    mirrored: bool,
}

impl<'a> Mapper0<'a> {

    pub fn from(cart: &'a mut Cartridge) -> Self {

        let mirrored = cart.rom.is_mirrored();

        Mapper0 {
            cart,
            mirrored,
        }
    }

}

impl<'a> IMapper for Mapper0<'a> {

    fn read(&self, address: u16) -> u8 {
        let address = address & 0xFFFF;

        if address < 0x2000 {
            // CHR
            return self.cart.rom.get_chr()[address as usize];
        } else if address >= 0x8000 {
            // PRG
            let address = if self.mirrored {address & 0xBFFF} else {address};
            return self.cart.rom.get_prg()[(address - 0x8000) as usize];
        } else if address >= 0x6000 {
            // SRAM
            return self.cart.sram[(address - 0x6000) as usize];
        }

        return 0;
    }

    fn write(&mut self, address: u16, data: u8) {
        let address = address & 0xFFFF;

        if address < 0x2000 {
            // CHR
            self.cart.rom.get_chr_mut()[address as usize] = data;
        } else if address >= 0x8000 {
            // PRG
            let address = if self.mirrored {address & 0xBFFF} else {address};
            self.cart.rom.get_prg_mut()[(address - 0x8000) as usize] = data;
        } else if address >= 0x6000 {
            // RAM
            self.cart.sram[(address - 0x6000) as usize] = data;
        }
    }

}