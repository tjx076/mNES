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
 *
 * https://www.nesdev.org/wiki/PPU_OAM
 * http://wiki.nesdev.com/w/index.php/PPU_sprite_evaluation
 *
 * PPU的内部寄存器，也叫做锁存器
 *
 */
public class PPU {


    IBus ppuBus;

    public PPU(IBus ppuBus) {
        this.ppuBus = ppuBus;
    }

}
