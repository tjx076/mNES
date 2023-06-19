
use crate::cartridge::Cartridge;
use crate::cpu::CPU;

pub struct Emulator {
    cart: Cartridge,
    cpu: CPU,
}

impl Emulator {
    pub fn clock() {
// 调用 CPU，PPU，APU 的 clock
    }
}