package org.wrpg.mnes.cpu;

/**
 * 中断向量
 */
enum InterruptVector {

    //0xFFFA, 0xFFFB: NMI 中断地址
    NMI((short) 0xFFFA),//不可屏蔽中断，该中断不能通过 P 的 I 标志屏蔽，所以它一定能触发。比如 PPU 在进入 VBlank 时就会产生 NMI 中断
    //0xFFFC, 0xFFFD: RESET 中断地址
    RESET((short) 0xFFFC),//复位中断，RESET 按钮按下后或者系统刚上电时产生
    //0xFFFE, 0xFFFF: IRQ 中断地址
    IRQ((short) 0xFFFE)//可屏蔽中断，如果 P 的 I 标志置 1，则可以屏蔽该中断，同时也可以通过 BRK 指令由软件自行触发
    ;

    public short address;

    InterruptVector(short address) {
        this.address = address;
    }

}
