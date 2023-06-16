
//https://www.nesdev.org/wiki/NES_2.0

use std::fs::File;
use std::io::Read;

const SRAM_SIZE: usize = 8 * 1024;
const VRAM_SIZE: usize = 2 * 1024;

const HEAD_SIZE: usize = 16;//byte
const TRAINER_SIZE: usize = 512;//byte
const PRG_ROM_UNITS: usize = 16*1024;//byte
const CHR_ROM_UNITS: usize = 8*1024;//byte

pub struct Cartridge {

    pub rom: Rom,

    //卡带上 8KB RAM，用于游戏额外的数据存储，比如：游戏存档
    pub sram: Vec<u8>,

    //2KB 的额外用于 PPU 用的 VRAM，PPU 工作在 4-Screen 的时候才会用到
    pub vram: Vec<u8>,
    
}

impl Cartridge {

    pub fn from(path: &str) -> Self {
        
        let mut file = File::open(path).unwrap();
        let mut data = vec![];
        file.read_to_end(&mut data).unwrap();

        let rom = Rom::from(&data);

        let sram = vec![];//TODO 

        let vram = vec![];//TODO 

        Cartridge {
            rom: rom,
            sram: sram,
            vram: vram,
        }
    }

}

pub struct Rom {

    header: Header,

    prg: Vec<u8>,

    chr: Vec<u8>,

}

impl Rom {

    fn from(rom_data: &[u8]) -> Self {

        let header = Header::from(&rom_data[..HEAD_SIZE]);
        
        //Trainer Area
        let trainer_len = if header.trainer { TRAINER_SIZE } else { 0 };

        //PRG-ROM Area
        let prg_len = header.prg_size as usize * PRG_ROM_UNITS;
        let prg_off = HEAD_SIZE + trainer_len;
        let prg = rom_data[prg_off..prg_off+prg_len].to_owned();

        //CHR-ROM Area
        let chr_len = header.chr_size as usize * CHR_ROM_UNITS;
        let chr_off = prg_off + prg_len;
        let chr = rom_data[chr_off..chr_off+chr_len].to_owned();

        Rom {
            header: header,
            prg: prg,
            chr: chr,
        }
    }


    pub fn get_mapperno(&self) -> u8 {
        self.header.mapper_no
    }

    pub fn is_mirrored(&self) -> bool {
        // 程序大小只有 16K 则需要镜像
        self.prg.len() == 16 * 1024
    }

    pub fn get_prg(&self) -> &[u8] {
        &self.prg[..]
    }

    pub fn get_chr(&self) -> &[u8] {
        &self.chr[..]
    }

    pub fn get_prg_mut(&mut self) -> &mut [u8] {
        &mut self.prg[..]
    }

    pub fn get_chr_mut(&mut self) -> &mut [u8] {
        &mut self.chr[..]
    }

}

// Offset Meaning
// --------------
// 0-3    Identification String. Must be "NES<EOF>".

// 4      PRG-ROM size LSB
// 5      CHR-ROM size LSB

// 6      Flags 6
//        D~7654 3210
//          ---------
//          NNNN FTBM
//          |||| |||+-- Hard-wired nametable mirroring type
//          |||| |||     0: Horizontal (vertical arrangement) or mapper-controlled
//          |||| |||     1: Vertical (horizontal arrangement)
//          |||| ||+--- "Battery" and other non-volatile memory
//          |||| ||      0: Not present
//          |||| ||      1: Present
//          |||| |+--- 512-byte Trainer
//          |||| |      0: Not present
//          |||| |      1: Present between Header and PRG-ROM data
//          |||| +---- Hard-wired four-screen mode
//          ||||        0: No
//          ||||        1: Yes
//          ++++------ Mapper Number D0..D3

// 7      Flags 7
//        D~7654 3210
//          ---------
//          NNNN 10TT
//          |||| ||++- Console type
//          |||| ||     0: Nintendo Entertainment System/Family Computer
//          |||| ||     1: Nintendo Vs. System
//          |||| ||     2: Nintendo Playchoice 10
//          |||| ||     3: Extended Console Type
//          |||| ++--- NES 2.0 identifier
//          ++++------ Mapper Number D4..D7

// 8      Mapper MSB/Submapper
//        D~7654 3210
//          ---------
//          SSSS NNNN
//          |||| ++++- Mapper number D8..D11
//          ++++------ Submapper number

// 9      PRG-ROM/CHR-ROM size MSB
//        D~7654 3210
//          ---------
//          CCCC PPPP
//          |||| ++++- PRG-ROM size MSB
//          ++++------ CHR-ROM size MSB

// 10     PRG-RAM/EEPROM size
//        D~7654 3210
//          ---------
//          pppp PPPP
//          |||| ++++- PRG-RAM (volatile) shift count
//          ++++------ PRG-NVRAM/EEPROM (non-volatile) shift count
//        If the shift count is zero, there is no PRG-(NV)RAM.
//        If the shift count is non-zero, the actual size is
//        "64 << shift count" bytes, i.e. 8192 bytes for a shift count of 7.

// 11     CHR-RAM size
//        D~7654 3210
//          ---------
//          cccc CCCC
//          |||| ++++- CHR-RAM size (volatile) shift count
//          ++++------ CHR-NVRAM size (non-volatile) shift count
//        If the shift count is zero, there is no CHR-(NV)RAM.
//        If the shift count is non-zero, the actual size is
//        "64 << shift count" bytes, i.e. 8192 bytes for a shift count of 7.

// 12     CPU/PPU Timing
//        D~7654 3210
//          ---------
//          .... ..VV
//                 ++- CPU/PPU timing mode
//                      0: RP2C02 ("NTSC NES")
//                      1: RP2C07 ("Licensed PAL NES")
//                      2: Multiple-region
//                      3: UA6538 ("Dendy")

// 13     When Byte 7 AND 3 =1: Vs. System Type
//        D~7654 3210
//          ---------
//          MMMM PPPP
//          |||| ++++- Vs. PPU Type
//          ++++------ Vs. Hardware Type

//        When Byte 7 AND 3 =3: Extended Console Type
//        D~7654 3210
//          ---------
//          .... CCCC
//               ++++- Extended Console Type

// 14     Miscellaneous ROMs
//        D~7654 3210
//          ---------
//          .... ..RR
//                 ++- Number of miscellaneous ROMs present

// 15     Default Expansion Device
//        D~7654 3210
//          ---------
//          ..DD DDDD
//            ++-++++- Default Expansion Device
#[derive(Default)]
struct Header {

    ines: bool,
    nes20: bool,

    prg_size: u8,
    chr_size: u8, 

    trainer: bool,
    mapper_no: u8,

}

impl Header {

    fn from(head_data: &[u8]) -> Self {
        
        let mut header = Header::default();

        if head_data[0]==b'N' && head_data[1]==b'E' && head_data[2]==b'S' && head_data[3]==0x1A {
            header.ines = true;

            if (head_data[7]&0x0C)==0x08 {
                header.nes20 = true;
            }
        }

        header.prg_size = head_data[4];
        header.chr_size = head_data[5];

        header.trainer = (head_data[6]&0x04)==0x04;
        header.mapper_no = (head_data[7]&0xF0) | ((head_data[6]>>4)&0x0F);

        header

    }




}
