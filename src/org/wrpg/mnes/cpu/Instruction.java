package org.wrpg.mnes.cpu;

/**
 * CPU指令
 *
 * http://www.oxyron.de/html/opcodes02.html
 * 里面包含了 56 种官方指令和一些非官方指令
 * 每个方块包含了指令，寻址模式，执行周期，
 * 例如： LDY imm 2
 * 表示：LDY 指令，立即寻址，执行完需要 2 个 CPU 时钟
 * 有些方块执行周期后面有个 * 号，这说明该指令在某些情况下需要额外增加 1 ~ 2 个时钟才能完成，具体等介绍完寻址模式再解释
 *
 *
 */
class Instruction {

    public Opcode opcode;//操作码(1个字节)
    public AddressingMode addressingMode;//寻址模式
    public byte dataBytes;//操作数(1个字节)+指令数据(不同寻址模式，占不同的字节数)
    public byte cycles;//指令周期

//        有两种情况会额外增加时钟
//
//        分支指令进行跳转时
//        分支指令比如 BNE，BEQ 这类指令，如果检测条件为真，这时需要额外增加 1 个时钟

    //        跨Page访问
//        新地址和旧地址如果 Page 不一样，即 (newAddr & 0xFF00) !== (oldAddr & 0xFF00)，则需要额外增加一个时钟。例如 0x1234 与 0x12FF 为同一 Page，但是与 0x1334 为不同 Page
//
    public byte pageCycles;

    Instruction(Opcode opcode,
                AddressingMode addressingMode,
                byte dataBytes,
                byte cycles,
                byte pageCycles) {
        this.opcode = opcode;
        this.addressingMode = addressingMode;
        this.dataBytes = dataBytes;
        this.cycles = cycles;
        this.pageCycles = pageCycles;
    }
}
