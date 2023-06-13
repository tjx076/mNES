package org.wrpg.mnes.cpu;

//http://www.oxyron.de/html/opcodes02.html
//指令集
public class InstructionSet {

    Instruction[] instructions = new Instruction[]{
            // http://nesdev.com/the%20%27B%27%20flag%20&%20BRK%20instruction.txt Says:
            //   Regardless of what ANY 6502 documentation says, BRK is a 2 byte opcode. The
            //   first is #$00, and the second is a padding byte. This explains why interrupt
            //   routines called by BRK always return 2 bytes after the actual BRK opcode,
            //   and not just 1.
            // So we use ZERO_PAGE instead of IMPLICIT addressing mode
            new Instruction(Opcode.BRK, AddressingMode.ZERO_PAGE, (byte) 2, (byte)7, (byte)0),//0
            new Instruction(Opcode.ORA, AddressingMode.X_INDEXED_INDIRECT, (byte) 2, (byte)6, (byte)0),// 1, 1h

    };

}
