package org.wrpg.mnes.cpu;

import org.wrpg.mnes.IBus;


/**
 * 首先系统上电或者 RESET 按钮按下后，会触发 RESET 中断，
 * CPU 从 0xFFFA 和 0xFFFB 存储的地址处（2byte）开始取指令运行
 * ，之后 CPU 会一直运行 0x8000 - 0xFFF9 区间的指令。
 *
 * 实际上PRG-ROM上的程序会让cpu，按下面方式运行：
 * 在每一帧渲染之前，CPU 会读取输入设备，
 * 然后通过 PPU 寄存器往 PPU 总线上的 VRAM 写数据，
 * 同时往 APU 写数据，最终反馈到了屏幕和声音上
 *
 */
public class CPU {

    long suspendCycles;

    IBus cpuBus;

    Registers registers;

    byte instructionCycles = 0;
    long clocks = 0;

    //http://www.oxyron.de/html/opcodes02.html
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


    public CPU(IBus cpuBus) {
        this.cpuBus = cpuBus;
        this.registers = new Registers();
    }

    public void clock() {
        if (this.suspendCycles > 0) {
            this.suspendCycles--;
            return;
        }

        if (this.instructionCycles == 0) {
            this.step();
        }

        this.instructionCycles--;
        this.clocks++;
    }

    private void step() {

    }

    public void reset() {
        this.registers.A = 0;
        this.registers.X = 0;
        this.registers.Y = 0;
        this.registers.P = 0;
        this.registers.SP = (byte) 0xfd;
        this.registers.PC = this.cpuBus.readWord(InterruptVector.RESET.address);

        this.instructionCycles = 8;
        this.clocks = 0;
    }

    public void irq() {
        if (this.isFlagSet(Flags.I)) {//屏蔽掉中断
            return;
        }

        this.pushWord(this.registers.PC);
        this.pushByte((byte)((this.registers.P | Flags.U.value) & ~Flags.B.value));

        this.setFlag(Flags.I, true);

        this.registers.PC = this.cpuBus.readWord(InterruptVector.IRQ.address);

        this.instructionCycles += 7;
    }

    public void nmi() {

    }

    private void setFlag(Flags flag, boolean value) {
        if (value) {
            this.registers.P |= flag.value;
        } else {
            this.registers.P &= ~flag.value;
        }
    }

    private boolean isFlagSet(Flags flag) {
        return !((this.registers.P & flag.value) == flag.value);
    }

    private void pushWord(short data) {
        this.pushByte((byte)(data >> 8));
        this.pushByte((byte)data);
    }

    private void pushByte(byte data) {
        this.cpuBus.writeByte((short) (0x100 + this.registers.SP), data);
        this.registers.SP = (byte)((this.registers.SP - 1) & 0xFF);
    }

    private short popWord() {
        return (short) (this.popByte() | this.popByte() << 8);
    }

    private byte popByte() {
        this.registers.SP = (byte)((this.registers.SP + 1) & 0xFF);
        return this.cpuBus.readByte((short)(0x100 + this.registers.SP));
    }


}
