package org.wrpg.mnes.cpu;

/**
 * CPU指令
 */
class Instruction {

    public Opcode opcode;//操作码
    public AddressingMode addressingMode;//寻址模式
    public byte dataBytes;//操作数，指令数据
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
