package org.wrpg.mnes.cpu;

/**
 * 标志寄存器的标志位
 * //BIT	名称	含义
 * //0	C	进位标志，如果计算结果产生进位，则置 1
 * //1	Z	零标志，如果结算结果为 0，则置 1
 * //2	I	中断去使能标志，置 1 则可屏蔽掉 IRQ 中断
 * //3	D	十进制模式，未使用
 * //4	B	BRK，后面解释
 * //5	U	未使用，后面解释
 * //6	V	溢出标志，如果结算结果产生了溢出，则置 1
 * //7	N	负标志，如果计算结果为负，则置 1
 *
 *
 *
 * //标志寄存器只有 6 bit，这是因为 B 和 U 并不是实际位，只不过某些指令执行后，'
 * 标志位 push 到 stack 的时候，会附加上这两位以区分中断是由 BRK 触发还是 IRQ 触发
 * //指令或中断	U和B的值	 push之后对P的影响
 * //PHP 指令	11	无
 * //BRK 指令	11	I置1
 * //IRQ 中断	10	I置1
 * //MNI 中断	10	I置1
 *
 */
enum Flags {
    C((byte)(1 << 0)), // Carry
    Z((byte)(1 << 1)), // Zero
    I((byte)(1 << 2)), // Disable interrupt
    D((byte)(1 << 3)), // Decimal Mode ( unused in nes )
    B((byte)(1 << 4)), // Break
    U((byte)(1 << 5)), // Unused ( always 1 )
    V((byte)(1 << 6)), // Overflow
    N((byte)(1 << 7)); // Negative

    byte value;

    Flags(byte v) {
        this.value = v;
    }

}
