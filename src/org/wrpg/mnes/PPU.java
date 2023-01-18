package org.wrpg.mnes;

public class PPU {

    private static final int  PATTERN_TABLE_1_SIZE = 4*1024;//byte
    private static final int  PATTERN_TABLE_2_SIZE = 4*1024;//byte
    private static final int  NAME_TABLE_0_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_0_SIZE = 64;//byte
    private static final int  NAME_TABLE_1_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_1_SIZE = 64;//byte
    private static final int  NAME_TABLE_2_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_2_SIZE = 64;//byte
    private static final int  NAME_TABLE_3_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_3_SIZE = 64;//byte

    private static final int NAME_TABLES_MIRRORS = NAME_TABLE_0_SIZE+ATTRIBUTE_TABLE_0_SIZE
                                                +NAME_TABLE_1_SIZE+ATTRIBUTE_TABLE_1_SIZE
                                                +NAME_TABLE_2_SIZE+ATTRIBUTE_TABLE_2_SIZE
                                                +NAME_TABLE_3_SIZE+ATTRIBUTE_TABLE_3_SIZE
                                                                    ;//0x3000 - 0x3EFF

    private static final int  IMAGE_PALETTE_SIZE = 16;//byte
    private static final int  SPRITE_PALETTE_SIZE = 16;//byte


    Cartridge cartridge;

    private byte[] nameTable0 = new byte[NAME_TABLE_0_SIZE];//$2000-$23C0
    private byte[] attributeTable0 = new byte[ATTRIBUTE_TABLE_0_SIZE];//$23C0-$2400
    private byte[] nameTable1 = new byte[NAME_TABLE_1_SIZE];//$2400-$27C0
    private byte[] attributeTable1 = new byte[ATTRIBUTE_TABLE_1_SIZE];//$27C0-$2800
    private byte[] nameTable2 = new byte[NAME_TABLE_2_SIZE];//$2800-$2BC0
    private byte[] attributeTable2 = new byte[ATTRIBUTE_TABLE_2_SIZE];//$2BC0-$2C00
    private byte[] nameTable3 = new byte[NAME_TABLE_3_SIZE];//$2C00-$2FC0
    private byte[] attributeTable3 = new byte[ATTRIBUTE_TABLE_3_SIZE];//$2FC0-$3000

    private byte[] imagePalette = new byte[IMAGE_PALETTE_SIZE];//$3F00-$3F10
    private byte[] spritePalette = new byte[SPRITE_PALETTE_SIZE];//$3F10-$3F20

    public PPU(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

}
