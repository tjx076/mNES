package org.wrpg.mnes;

public class Emulator implements IEmulator{

    private Cartridge cartridge;
    private CPU cpu;
    private PPU ppu;


    public Emulator(byte[] nesData) {
        this.cartridge = new Cartridge(nesData, null);
        this.cpu = new CPU(cartridge.getMapper());
        this.ppu = new PPU(cartridge.getMapper());

    }

    @Override
    public void onSample(int volume) {

    }

    @Override
    public void onFrame(byte[] frame) {

    }

    @Override
    public void clock() {
// 调用 CPU，PPU，APU 的 clock
    }
}
