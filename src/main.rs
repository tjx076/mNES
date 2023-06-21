pub mod mapper;
pub mod bus;
pub mod cartridge;
pub mod cpu;
pub mod emulator;

#[macro_use]
extern crate lazy_static;

use crate::cartridge::Cartridge;
use crate::mapper::mapper0::Mapper0;

fn main() {
    let cartridge = Cartridge::from("/home/tjx/workspace/github_workspace/mNES/res/nestest.nes");

    let mapper = Mapper0::from(&cartridge);
    

    println!("Hello, world!");
}
