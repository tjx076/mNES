package org.wrpg.mnes.cpu;

/**
 * http://www.oxyron.de/html/opcodes02.html
 * 操作码
 */
enum Opcode {

    ADC, AND, ASL, BCC, BCS, BEQ, BIT, BMI,
    BNE, BPL, BRK, BVC, BVS, CLC, CLD, CLI,
    CLV, CMP, CPX, CPY, DEC, DEX, DEY, EOR,
    INC, INX, INY, JMP, JSR, LDA, LDX, LDY,
    LSR, NOP, ORA, PHA, PHP, PLA, PLP, ROL,
    ROR, RTI, RTS, SBC, SEC, SED, SEI, STA,
    STX, STY, TAX, TAY, TSX, TXA, TXS, TYA,

    // Illegal opcode
    DCP, ISC, LAX, RLA, RRA, SAX, SLO, SRE,

    INVALID;

}
