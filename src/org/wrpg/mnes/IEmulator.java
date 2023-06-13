package org.wrpg.mnes;

public interface IEmulator {

    void onSample(int volume);

    void onFrame(byte[] frame);

    void clock();

}
