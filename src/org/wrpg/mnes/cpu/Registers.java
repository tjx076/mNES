package org.wrpg.mnes.cpu;

/**
 * 寄存器
 */
class Registers {

    public short PC = 0;//程序计数器，记录下一条指令地址

    public byte SP = 0;//堆栈寄存器，其值为 0x00 ~ 0xFF，对应着 CPU 总线上的 0x100 ~ 0x1FF

//BIT	名称	含义
//0	C	进位标志，如果计算结果产生进位，则置 1
//1	Z	零标志，如果结算结果为 0，则置 1
//2	I	中断去使能标志，置 1 则可屏蔽掉 IRQ 中断
//3	D	十进制模式，未使用
//4	B	BRK，后面解释
//5	U	未使用，后面解释
//6	V	溢出标志，如果结算结果产生了溢出，则置 1
//7	N	负标志，如果计算结果为负，则置 1

    //        标志寄存器只有 6 bit，这是因为 B 和 U 并不是实际位，只不过某些指令执行后，标志位 push 到 stack 的时候，会附加上这两位以区分中断是由 BRK 触发还是 IRQ 触发
//指令或中断	U和B的值	 push之后对P的影响
//PHP 指令	11	无
//BRK 指令	11	I置1
//IRQ 中断	10	I置1
//MNI 中断	10	I置1
    public byte P = 0;//标志寄存器比较麻烦，它实际上只有 6bit，但是我们可以看成 8bit

    public byte A = 0;//通常作为累加器

    public byte X = 0;//通常作为循环计数器

    public byte Y = 0;//通常作为循环计数器

}
