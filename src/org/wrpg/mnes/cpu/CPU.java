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
 *
 * CPU 有一个时钟作为输入源，
 * 该时钟实际上只是一个频率很高的脉冲波，一般几 M 到 几 GHZ。
 * 传统的 CPU 会在一个到多个时钟期间执行完一条指令，然后再执行下一条指令。
 * 如果某一时刻产生了中断，CPU 会读取中断向量表对应中断地址，执行中断向量程序
 * 等到中断执行完后切换回中断前地址处继续执行
 *
 *
 */
public class CPU {

    long suspendCycles;//暂停的指令周期

    IBus cpuBus;

    Registers registers;

    byte instructionCycles = 0;
    long clocks = 0;



    public CPU(IBus cpuBus) {
        this.cpuBus = cpuBus;
        this.registers = new Registers();
    }

    /**
     * CPU 时钟 1.79 MHz
     */
    public void clock() {
        if (this.suspendCycles > 0) {
            this.suspendCycles--;
            return;
        }

        if (this.instructionCycles == 0) {
            //正常物理CPU，一个指令会分成多个阶段来执行，比如：取指令、解析、存取数等
            //所以一个指令是分成多个时钟周期，分步执行完的
            //但对于模拟器，cpu指令的执行，我们就只在cpu指令周期为0的时候，执行它
            this.step();
        }

        this.instructionCycles--;
        this.clocks++;
    }

    /**
     * 执行指令
     *
     */
    private void step() {

    }

    /**
     * reset中断
     */
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

    /**
     * 可屏蔽中断
     */
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

    /**
     * 不可屏蔽中断
     */
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
