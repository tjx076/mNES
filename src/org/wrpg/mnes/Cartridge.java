package org.wrpg.mnes;

public class Cartridge {

    ROM rom;

    byte[] sram;

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
