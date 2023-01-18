package org.wrpg.mnes;

public interface IMapper {

    public byte read(short address);
    public void write(short address, byte data);

}
