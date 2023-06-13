package org.wrpg.mnes.ppu;

/**
 * Attribute table 跟在每个 Name table 之后，每个 Attribute table 有 64 字节
 * Attribute table 控制着当前图像高 2bit 的 palette 偏移量，而 Name table 索引到 Pattern table 数据之后，控制着低 2bit 的 palette 偏移量。两个加起来刚好 4bit，前一章节讲过，背景和精灵分别有 16 个 palette ，刚好对应了 4bit
 *
 * 颜色高 2bit 由 attribute table 表示，
 * attribute table 中每一个字节能管理 16 个 tile 的颜色，
 * 16 个 tile 中，每 4 个 tile 整合到一起，再和另外 12 个 tile 组成田字形格局，
 * 分别为 左上，右上，左下，右下。每个占 2 bit，刚好 8 bit
 *
 * attribute table共占64byte，一个字节管理16个tile，所以
 * attribute table共能管理1024个tile
 *
 * 而一个图片是由960个tile组成，所以完全够用
 *
 */
public class AttributeTable {

    private static final int  ATTRIBUTE_TABLE_SIZE = 64;//byte

}
