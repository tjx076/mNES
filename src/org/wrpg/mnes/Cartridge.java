package org.wrpg.mnes;

import org.wrpg.mnes.rom.Mapper0;
import org.wrpg.mnes.rom.ROM;

public class Cartridge {

    ROM rom;

    /**
     * 卡带上 8KB RAM
     * 用于游戏额外的数据存储，最重要的是它能够保存游戏存档。这也说明了为什么我们拔了卡带上的电池后存档就没了
     */
    byte[] sram;

    /**
     * 有的卡带还带了 2KB 的额外用于 PPU 用的 VRAM
     * PPU 工作在 4-Screen 的时候才会用到
     */
    byte[] vram;

    IMapper mapper;

    public Cartridge(byte[] romData, byte[] sram) {
        this.rom = new ROM(romData);

        this.sram = sram;

        int mapperNo = rom.getMapper();
        if(mapperNo == 0) {
            mapper = new Mapper0(rom, sram);
        }
    }

    public IMapper getMapper() {
        return mapper;
    }
}
