package org.wrpg.mnes.rom;

import org.wrpg.mnes.IMapper;
import org.wrpg.mnes.rom.ROM;

public class Mapper0 implements IMapper {

    ROM rom;

    byte[] sram;

    private boolean isMirrored;

    public Mapper0(ROM rom, byte[] sram) {
        this.rom = rom;
        this.sram = sram;
        this.isMirrored = rom.getPrgROMSize() == 16 * 1024; // 程序大小只有 16K 则需要镜像
    }

    @Override
    public byte read(short address) {
        address &= 0xFFFF;

        if (address < 0x2000) {
            // CHR
            return this.rom.getChrROM()[address];
        } else if (address >= 0x8000) {
            // PRG
            return this.rom.getPrgROM()[(this.isMirrored ? address & 0xBFFF : address) - 0x8000];
        } else if (address >= 0x6000) {
            // SRAM
            return this.sram[address - 0x6000];
        }

        return 0;
    }

    @Override
    public void write(short address, byte data) {
        address &= 0xFFFF;

        if (address < 0x2000) {
            // CHR
            this.rom.getChrROM()[address] = data;
        } else if (address >= 0x8000) {
            // PRG
            this.rom.getPrgROM()[(this.isMirrored ? address & 0xBFFF : address) - 0x8000] = data;
        } else if (address >= 0x6000) {
            // RAM
            this.sram[address - 0x6000] = data;
        }
    }
}
