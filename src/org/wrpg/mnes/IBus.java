package org.wrpg.mnes;

public interface IBus {

    void writeByte(short address, byte data);
    void writeWord(short address, short data);
    byte readByte(short address);
    short readWord(short address);

}
