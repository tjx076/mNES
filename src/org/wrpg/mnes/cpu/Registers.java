package org.wrpg.mnes.cpu;

/**
 * 寄存器
 */
class Registers {

    public short PC = 0;//程序计数器，记录下一条指令地址

    public byte SP = 0;//堆栈寄存器，其值为 0x00 ~ 0xFF，对应着 CPU 总线上的 0x100 ~ 0x1FF

    public byte P = 0;//标志寄存器比较麻烦，它实际上只有 6bit，但是我们可以看成 8bit

    public byte A = 0;//通常作为累加器

    public byte X = 0;//通常作为循环计数器

    public byte Y = 0;//通常作为循环计数器

}
