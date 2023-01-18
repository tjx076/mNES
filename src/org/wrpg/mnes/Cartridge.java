package org.wrpg.mnes;

public class Cartridge {

    ROM rom;
    IMapper mapper;

    public Cartridge(byte[] romData, byte[] sram) {
        this.rom = new ROM(romData, sram);

        int mapperNo = rom.getMapper();
        if(mapperNo == 0) {
            mapper = new Mapper0(rom);
        }
    }

    public byte read(short address) {
        return mapper.read(address);
    }
    public void write(short address, byte data) {
        mapper.write(address, data);
    }
}
