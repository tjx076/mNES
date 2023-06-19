
use crate::cartridge::Cartridge;
use super::IMapper;

pub struct Mapper0<'a> {
    cart: &'a Cartridge,
    mirrored: bool,
}

impl<'a> Mapper0<'a> {
    pub fn from(cart: &'a Cartridge) -> Self {
        let mirrored = cart.is_mirrored();

        Mapper0 {
            cart,
            mirrored,
        }
    }
}

impl<'a> IMapper for Mapper0<'a> {
    fn read(&self, address: u16) -> u8 {
        let address: u16 = address & 0xFFFF;

        if address < 0x2000 {
            // CHR
            let rom = self.cart.rom.borrow();
            return rom.get_chr()[address as usize];
        } else if address >= 0x8000 {
            // PRG
            let address = if self.mirrored {address & 0xBFFF} else {address};
            let rom = self.cart.rom.borrow();
            return rom.get_prg()[(address - 0x8000) as usize];
        } else if address >= 0x6000 {
            // SRAM
            let sram = self.cart.sram.borrow();
            return sram[(address - 0x6000) as usize];
        }

        return 0;
    }

    fn write(&mut self, address: u16, data: u8) {
        let address = address & 0xFFFF;

        if address < 0x2000 {
            // CHR
            let mut rom = self.cart.rom.borrow_mut();
            rom.get_chr_mut()[address as usize] = data;
        } else if address >= 0x8000 {
            // PRG
            let address = if self.mirrored {address & 0xBFFF} else {address};
            let mut rom = self.cart.rom.borrow_mut();
            rom.get_prg_mut()[(address - 0x8000) as usize] = data;
        } else if address >= 0x6000 {
            // RAM
            let mut sram = self.cart.sram.borrow_mut();
            sram[(address - 0x6000) as usize] = data;
        }
    }

}