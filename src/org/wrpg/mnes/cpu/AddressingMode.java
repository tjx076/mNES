package org.wrpg.mnes.cpu;

//http://obelisk.me.uk/6502/addressing.html#IMP
//    寻址模式	含义	数据长度	例子	说明
//    Implicit	特殊指令的寻址方式	0	CLC	清除 C 标志
//    Accumulator	累加器 A 寻址	0	LSR A	A 右移一位
//    Immediate	指定一个字节的数据	1	ADC #$1	A 增加 1
//    Zero Page	指定 Zero Page 地址	1	LDA $00	将 0x0000 的值写入 A
//    Zero Page,X	指定 Zero Page 地址加上 X	1	STY $10,X	将 0x0010 + X 地址上的值写入 Y
//    Zero Page,Y	指定 Zero Page 地址加上 Y	1	LDX $10,Y	将 0x0010 + X 地址上的值写入 X
//    Relative	相对寻址	1	BEQ LABEL	如果 Z 标志置位则跳转到 LABEL 所在地址，跳转范围为当前 PC 的 -128 ~ 127
//    Absolute	绝对寻址	2	JMP $1234	跳转到地址 0x1234
//    Absolute,X	绝对寻址加上 X	2	STA $3000,X	将 0x3000 + X 地址值写入 A
//    Absolute,Y	绝对寻址加上 Y	2	STA $3000,Y	将 0x3000 + Y 地址值写入 A
//    Indirect	间接寻址（只有 JMP 使用）	2	JMP ($FFFC)	跳转到 0xFFFC 地址上的值表示的地址处
//    Indexed Indirect	变址间接寻址	1	LDA ($40,X)	首先 X 的值加上 0x40，得到一个地址，再以此地址上的值作为一个新地址，将新地址上的值写入 A
//    Indirect Indexed	间接变址寻址	1	LDA ($40),Y	首先获取 0x40 处存储的值，将该值与 Y 相加，得到一个新地址，然后将该地址上的值写入 A
enum AddressingMode {

    IMPLICIT, // CLC | RTS
    ACCUMULATOR, // LSR A
    IMMEDIATE, // LDA #10
    ZERO_PAGE, // LDA $00
    ZERO_PAGE_X, // STY $10, X
    ZERO_PAGE_Y, // LDX $10, Y
    RELATIVE, // BEQ label | BNE *+4
    ABSOLUTE, // JMP $1234
    ABSOLUTE_X, // STA $3000, X
    ABSOLUTE_Y, // AND $4000, Y
    INDIRECT, // JMP ($FFFC)
    X_INDEXED_INDIRECT, // LDA ($40, X)
    INDIRECT_Y_INDEXED, // LDA ($40), Y

}
