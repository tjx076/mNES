package org.wrpg.mnes.ppu;

import org.wrpg.mnes.IBus;
import org.wrpg.mnes.IMapper;

/**
 * 渲染逻辑
 * https://www.nesdev.org/wiki/PPU_rendering
 *
 * 滚动逻辑
 * https://www.nesdev.org/wiki/PPU_scrolling
 *
 * 寄存器
 * https://www.nesdev.org/wiki/PPU_registers
 *
 */
public class PPU {


    IBus ppuBus;

    public PPU(IBus ppuBus) {
        this.ppuBus = ppuBus;
    }

}
