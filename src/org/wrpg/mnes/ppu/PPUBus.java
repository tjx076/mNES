package org.wrpg.mnes.ppu;

import org.wrpg.mnes.Cartridge;
import org.wrpg.mnes.IBus;
import org.wrpg.mnes.IMapper;

/**
 * https://www.nesdev.org/wiki/PPU_memory_map
 *
 * $0000-$0FFF	$1000	Pattern table 0
 * $1000-$1FFF	$1000	Pattern table 1
 * $2000-$23FF	$0400	Nametable 0
 * $2400-$27FF	$0400	Nametable 1
 * $2800-$2BFF	$0400	Nametable 2
 * $2C00-$2FFF	$0400	Nametable 3
 * $3000-$3EFF	$0F00	Mirrors of $2000-$2EFF
 * $3F00-$3F1F	$0020	Palette RAM indexes
 * $3F20-$3FFF	$00E0	Mirrors of $3F00-$3F1F
 *
 *
 */
public class PPUBus  implements IBus {

    /**
     * 0x2000 - 0x2FFF ( Name Tables )
     * 这里一共 4KB 数据，其中 2KB 为主机 VRAM，
     * 另外 2KB 根据游戏配置为前 2KB 的 Mirror 或者卡带上的 VRAM。
     * 这里面存放着 Pattern Table 的偏移量，以此控制屏幕显示的内容
     *
     */
    private static final int  NAME_TABLE_0_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_0_SIZE = 64;//byte
    private static final int  NAME_TABLE_1_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_1_SIZE = 64;//byte
    private static final int  NAME_TABLE_2_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_2_SIZE = 64;//byte
    private static final int  NAME_TABLE_3_SIZE = 960;//byte
    private static final int  ATTRIBUTE_TABLE_3_SIZE = 64;//byte

    /**
     * 0x3000 - 0x3EFF ( Mirrors )
     */
    private static final int NAME_TABLES_MIRRORS = NAME_TABLE_0_SIZE+ATTRIBUTE_TABLE_0_SIZE
            +NAME_TABLE_1_SIZE+ATTRIBUTE_TABLE_1_SIZE
            +NAME_TABLE_2_SIZE+ATTRIBUTE_TABLE_2_SIZE
            +NAME_TABLE_3_SIZE+ATTRIBUTE_TABLE_3_SIZE
            ;//0x3000 - 0x3EFF


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

    private Cartridge cartridge;

    public PPUBus(Cartridge cartridge) {
        this.cartridge = cartridge;
    }

    @Override
    public void writeByte(short address, byte data) {

    }

    @Override
    public void writeWord(short address, short data) {

    }

    @Override
    public byte readByte(short address) {
        return 0;
    }

    @Override
    public short readWord(short address) {
        return 0;
    }
}