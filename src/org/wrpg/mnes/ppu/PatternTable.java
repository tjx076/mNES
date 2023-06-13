package org.wrpg.mnes.ppu;

/**
 * 背景和精灵的PatternTable分别占4K
 *
 * pattern table 以 16 bytes 为单位，
 * 这16个字节，代表了64个像素，一个tile(瓦片)
 * 所以，背景和精灵，分别各自支持256个瓦片
 *
 *
 * 每 16 bytes 中，有分前 8 bytes 和后 8 bytes。
 * 前后每 8 bytes 表示 tile 中每一行的 像素所处 palette 的低 2 bit，
 * 前 8 bytes 为 bit0，后 8 byts 为 bit 1
 *
 */
public class PatternTable {

    /**
     * 0x0000 - 0x1FFF ( Pattern Tables )
     * 这里存放了 8KB 的图像数据，该区域位于卡带上，由 Mapper 管理着。
     * 它的作用是用来 PPU 渲染图像的时候作为参考。
     * 有的游戏里面这块区域是 RAM，由 CPU 写入图像数据
     *
     * Pattern Tables，分背景和精灵2部分
     * 每个像素点经过编码后，索引调色板
     */
    public static final int  PATTERN_TABLE_SIZE = 4*1024;//byte


}
