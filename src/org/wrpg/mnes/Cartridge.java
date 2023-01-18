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

    public byte read(short address) {
        return mapper.read(address);
    }
    public void write(short address, byte data) {
        mapper.write(address, data);
    }
}
