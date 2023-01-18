package org.wrpg.mnes;

import org.wrpg.mnes.utils.HexUtils;

import java.io.*;

/**
 * https://www.nesdev.org/wiki/NES_2.0
 */
public class ROM {

    public static final int HEAD_SIZE = 16;//byte
    private static final int TRAINER_SIZE = 512;//byte
    private static final int PRG_ROM_UNITS = 16*1024;//byte

    private static final int CHR_ROM_UNITS = 8*1024;//byte

    private Header header;

    private byte[] prgROM;

    private byte[] chrROM;

    public ROM(byte[] romData) {

        byte[] headData = new byte[HEAD_SIZE];
        System.arraycopy(romData, 0, headData, 0, headData.length);
        header = new Header(headData);

        int trainerLength = header.trainerPresent() ? TRAINER_SIZE : 0;

        int prgROMSizeIn16KB = header.getPrgROMSize();
        prgROM = new byte[prgROMSizeIn16KB*PRG_ROM_UNITS];
        int prgROMOffset = headData.length + trainerLength;
        System.arraycopy(romData, prgROMOffset, prgROM, 0, prgROM.length);

        int chrROMSizeIn8KB = header.getChrROMSize();
        chrROM = new byte[chrROMSizeIn8KB*CHR_ROM_UNITS];
        int chrROMOffset = headData.length + trainerLength + prgROM.length;
        System.arraycopy(romData, chrROMOffset, chrROM, 0, chrROM.length);

    }

    public static ROM loadFromFile(String filePath) throws Exception {

        byte[] data = file2Bytes(filePath);

        return new ROM(data);
    }

    public static byte[] file2Bytes(String filePath) throws Exception {
        FileInputStream in =new FileInputStream(new File(filePath));

        byte[] data=new byte[in.available()];
        in.read(data);

        in.close();

        return data;
    }

    public void prettyPrint() {
        header.prettyPrint();

        System.out.println(HexUtils.prettyPrintBytes("PRG-ROM", prgROM));

        System.out.println(HexUtils.prettyPrintBytes("CHR-ROM", chrROM));
    }

    public int getMapper() {
        return header.getMapper();
    }

    public byte[] getPrgROM() {
        return prgROM;
    }

    public int getPrgROMSize() {
        return prgROM.length;
    }

    public byte[] getChrROM() {
        return chrROM;
    }


    /**
     * Offset Meaning
     * --------------
     * 0-3    Identification String. Must be "NES<EOF>".
     *
     * 4      PRG-ROM size LSB
     * 5      CHR-ROM size LSB
     *
     * 6      Flags 6
     *        D~7654 3210
     *          ---------
     *          NNNN FTBM
     *          |||| |||+-- Hard-wired nametable mirroring type
     *          |||| |||     0: Horizontal or mapper-controlled
     *          |||| |||     1: Vertical
     *          |||| ||+--- "Battery" and other non-volatile memory
     *          |||| ||      0: Not present
     *          |||| ||      1: Present
     *          |||| |+--- 512-byte Trainer
     *          |||| |      0: Not present
     *          |||| |      1: Present between Header and PRG-ROM data
     *          |||| +---- Hard-wired four-screen mode
     *          ||||        0: No
     *          ||||        1: Yes
     *          ++++------ Mapper Number D0..D3
     *
     * 7      Flags 7
     *        D~7654 3210
     *          ---------
     *          NNNN 10TT
     *          |||| ||++- Console type
     *          |||| ||     0: Nintendo Entertainment System/Family Computer
     *          |||| ||     1: Nintendo Vs. System
     *          |||| ||     2: Nintendo Playchoice 10
     *          |||| ||     3: Extended Console Type
     *          |||| ++--- NES 2.0 identifier
     *          ++++------ Mapper Number D4..D7
     *
     * 8      Mapper MSB/Submapper
     *        D~7654 3210
     *          ---------
     *          SSSS NNNN
     *          |||| ++++- Mapper number D8..D11
     *          ++++------ Submapper number
     *
     * 9      PRG-ROM/CHR-ROM size MSB
     *        D~7654 3210
     *          ---------
     *          CCCC PPPP
     *          |||| ++++- PRG-ROM size MSB
     *          ++++------ CHR-ROM size MSB
     *
     * 10     PRG-RAM/EEPROM size
     *        D~7654 3210
     *          ---------
     *          pppp PPPP
     *          |||| ++++- PRG-RAM (volatile) shift count
     *          ++++------ PRG-NVRAM/EEPROM (non-volatile) shift count
     *        If the shift count is zero, there is no PRG-(NV)RAM.
     *        If the shift count is non-zero, the actual size is
     *        "64 << shift count" bytes, i.e. 8192 bytes for a shift count of 7.
     *
     * 11     CHR-RAM size
     *        D~7654 3210
     *          ---------
     *          cccc CCCC
     *          |||| ++++- CHR-RAM size (volatile) shift count
     *          ++++------ CHR-NVRAM size (non-volatile) shift count
     *        If the shift count is zero, there is no CHR-(NV)RAM.
     *        If the shift count is non-zero, the actual size is
     *        "64 << shift count" bytes, i.e. 8192 bytes for a shift count of 7.
     *
     * 12     CPU/PPU Timing
     *        D~7654 3210
     *          ---------
     *          .... ..VV
     *                 ++- CPU/PPU timing mode
     *                      0: RP2C02 ("NTSC NES")
     *                      1: RP2C07 ("Licensed PAL NES")
     *                      2: Multiple-region
     *                      3: UMC 6527P ("Dendy")
     *
     * 13     When Byte 7 AND 3 =1: Vs. System Type
     *        D~7654 3210
     *          ---------
     *          MMMM PPPP
     *          |||| ++++- Vs. PPU Type
     *          ++++------ Vs. Hardware Type
     *
     *        When Byte 7 AND 3 =3: Extended Console Type
     *        D~7654 3210
     *          ---------
     *          .... CCCC
     *               ++++- Extended Console Type
     *
     * 14     Miscellaneous ROMs
     *        D~7654 3210
     *          ---------
     *          .... ..RR
     *                 ++- Number of miscellaneous ROMs present
     *
     * 15     Default Expansion Device
     *        D~7654 3210
     *          ---------
     *          ..DD DDDD
     *            ++-++++- Default Expansion Device
     */
    private static class Header {

        boolean iNESFormat=false;
        boolean NES20Format = false;

        private byte[] headData;

        public Header(byte[] headData) {
            this.headData = headData;

            if (headData[0]=='N' && headData[1]=='E' && headData[2]=='S' && headData[3]==0x1A) {
                iNESFormat=true;
            }

            if (iNESFormat==true && (headData[7]&0x0C)==0x08) {
                NES20Format=true;
            }
        }

        public int getPrgROMSize() {
            return headData[4];
        }

        public int getChrROMSize() {
            return headData[5];
        }

        public boolean isVertical() {
            return (headData[6]&0x01)==0x01;
        }

        public boolean trainerPresent() {
            return (headData[6]&0x04)==0x04;
        }

        public boolean sramPresent()  {
            return (headData[6]&0x02)==0x02;
        }

        public boolean screen4()  {
            return (headData[6]&0x08)==0x08;
        }

        public int getMapper() {
            return (headData[7]&0xF0) | ((headData[6]>>4)&0x0F);
        }

        public void prettyPrint() {
            System.out.println(HexUtils.prettyPrintBytes("HEAD", headData));

            System.out.println("trainerPresent: "+trainerPresent());

            System.out.println("prgROMSizeIn16KB: "+getPrgROMSize());

            System.out.println("chrROMSizeIn8KB: "+getChrROMSize());

        }
    }
}
