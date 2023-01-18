package org.wrpg.mnes;

public interface IBus {

    void writeByte(short address, byte data);
    void writeWord(short address, byte[] data);
    byte readByte(short address);
    byte[] readWord(short address);

}
