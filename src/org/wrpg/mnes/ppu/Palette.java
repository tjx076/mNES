package org.wrpg.mnes.ppu;

import java.awt.*;

/**
 * 0x3F00 - 0x3F1F ( Palettes )
 * 这里是 NES 调色板数据，用于控制图像上每个像素的颜色
 * 一个颜色占一个字节
 *
 * NES 为了节省内存，并非支持所有颜色，而是有一套自己的映射，一共 64 种颜色
 *
 * 背景调色板位于 0x3F00 - 0x3F0F
 * 精灵调色板位于 0x3F10 - 0x3F1F
 *
 * 背景和精灵调色板占 16 个字节，即只支持16个颜色
 */
public class Palette {

    private static final int  PALETTE_SIZE = 16;//byte


    private short baseAddress;

}
