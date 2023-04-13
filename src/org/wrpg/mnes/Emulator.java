package org.wrpg.mnes;

import org.wrpg.mnes.cpu.CPU;
import org.wrpg.mnes.cpu.CPUBus;
import org.wrpg.mnes.ppu.PPU;
import org.wrpg.mnes.ppu.PPUBus;

public class Emulator implements IEmulator{

    private Cartridge cartridge;
    private CPU cpu;

    private IBus cpuBus;

    private PPU ppu;

    private IBus ppuBus;

    public Emulator(byte[] nesData) {

        this.cartridge = new Cartridge(nesData, null);

        this.cpuBus = new CPUBus(cartridge);
        this.cpu = new CPU(cpuBus);

        this.ppuBus = new PPUBus(cartridge);
        this.ppu = new PPU(ppuBus);


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
