
#[derive(Default)]
pub struct Registers {
    pub PC: u16,//程序计数器，记录下一条指令地址
    pub SP: u8,//堆栈寄存器，其值为 0x00 ~ 0xFF，对应着 CPU 总线上的 0x100 ~ 0x1FF
    pub P: u8,//标志寄存器
    pub A: u8,//通常作为累加器
    pub X: u8,//通常作为循环计数器
    pub Y: u8,//通常作为循环计数器
}

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
 * //标志寄存器只有 6 bit，这是因为 B 和 U 并不是实际位，只不过某些指令执行后，
 * 标志位 push 到 stack 的时候，会附加上这两位以区分中断是由 BRK 触发还是 IRQ 触发
 * //指令或中断	U和B的值	 push之后对P的影响
 * //PHP 指令	11	无
 * //BRK 指令	11	I置1
 * //IRQ 中断	10	I置1
 * //MNI 中断	10	I置1
 */
pub enum Flags {
    C = (1 << 0), // carry flag (1 on unsigned overflow)
    Z = (1 << 1), // zero flag (1 when all bits of a result are 0)
    I = (1 << 2), // IRQ flag (when 1, no interupts will occur (exceptions are IRQs forced by BRK and NMIs))
    D = (1 << 3), // decimal flag (1 when CPU in BCD mode)
    B = (1 << 4), // break flag (1 when interupt was caused by a BRK)
    U = (1 << 5), // unused (always 1)
    V = (1 << 6), // overflow flag (1 on signed overflow)
    N = (1 << 7), // negative flag (1 when result is negative)
}