package org.wrpg.mnes.ppu;

/**
 * Name table 数据做为 Pattern table 的索引，以此达到压缩数据的目的
 * 另外需要注意的是，每一块 Name table 只有 960 字节，而非 1KB，剩下的 64 字节为下面要介绍的 Attribute table
 *
 * 一帧图片是256 * 240个像素
 * 图片由瓦片组成，一个tile(瓦片)是64个像素
 * 一帧图片由32 x 30=960个瓦片组成
 *
 * 在Name table中一个字节代表一个瓦片
 *
 */
public class NameTable {

    /**
     * 0x2000 - 0x2FFF ( Name Tables )
     * 这里一共 4KB 数据，其中 2KB 为主机 VRAM，
     * 另外 2KB 根据游戏配置为前 2KB 的 Mirror 或者卡带上的 VRAM。
     * 这里面存放着 Pattern Table 的偏移量，以此控制屏幕显示的内容
     *
     */
    private static final int  NAME_TABLE_SIZE = 960;//byte

}
