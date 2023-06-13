package org.wrpg.mnes.tests;

import org.wrpg.mnes.rom.ROM;

public class ROMTests {

    public static void test_loadFromFile() throws Exception {
        ROM rom = ROM.loadFromFile("res/nestest.nes");
        rom.prettyPrint();
    }

    public static void main(String[] args) throws Exception {
        test_loadFromFile();

    }
}
