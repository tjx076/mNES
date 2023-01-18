package org.wrpg.mnes;

/**
 *
 * https://www.nesdev.org/wiki/CPU_memory_map
 * https://www.nesdev.org/nestech_cn.txt
 * 6502/6510/8500/8502 Opcode matrix: http://www.oxyron.de/html/opcodes02.html
 * https://www.jianshu.com/p/ba75b1186ecd
 *
 * NROM:
 * https://www.nesdev.org/wiki/NROM
 * https://www.nesdev.org/wiki/Programming_NROM
 *
 *     +---------+-------+-------+-----------------------+
 *     | 地址    | 大小  | 标记  |         描述          |
 *     +---------+-------+-------+-----------------------+
 *     | $0000   | $800  |       | RAM                   |
 *     | $0800   | $800  | M     | RAM                   |
 *     | $1000   | $800  | M     | RAM                   |
 *     | $1800   | $800  | M     | RAM                   |
 *     | $2000   | 8     |       | Registers             |
 *     | $2008   | $1FF8 |  R    | Registers             |
 *     | $4000   | $20   |       | Registers             |
 *     | $4020   | $1FDF |       | Expansion ROM         |
 *     | $6000   | $2000 |       | SRAM                  |
 *     | $8000   | $4000 |       | PRG-ROM               |
 *     | $C000   | $4000 |       | PRG-ROM               |
 *     +---------+-------+-------+-----------------------+
 *            标记图例: M = $0000的镜像
 *                         R = $2000-2008 每 8 bytes 的镜像
 *                             (e.g. $2008=$2000, $2018=$2000, etc.)
 */
public class CPU {

    private static final int RAM_SIZE = 2*1024;//byte
    private static final int RAM_ZERO_PAGE = 256;//byte
    private static final int RAM_STACK = 256;//byte
    private static final int RAM_OTHER = RAM_SIZE - RAM_ZERO_PAGE - RAM_STACK;//byte

    private static final int IO_REGISTERS_1_SIZE = 8;//byte
    private static final int IO_REGISTERS_2_SIZE = 32;//byte


    private byte[] ram = new byte[RAM_SIZE];//$0000-$0800
//    private byte[] mirror1 = new byte[RAM_SIZE];
//    private byte[] mirror2 = new byte[RAM_SIZE];
//    private byte[] mirror3 = new byte[RAM_SIZE];

    private byte[] ioRegisters1 = new byte[IO_REGISTERS_1_SIZE];//$2000-2008
    private byte[] ioRegisters2 = new byte[IO_REGISTERS_2_SIZE];//$4000-$4020

    long suspendCycles;

    IMapper mapper;

    IBus cpuBus;

    Registers registers;

    byte instructionCycles = 0;
    long clocks = 0;


    public CPU(IMapper mapper) {
        this.mapper = mapper;
        this.cpuBus = new CPUBus(mapper);
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

    private enum InterruptVector {

        //0xFFFA, 0xFFFB: NMI 中断地址
        NMI((short) 0xFFFA),//不可屏蔽中断，该中断不能通过 P 的 I 标志屏蔽，所以它一定能触发。比如 PPU 在进入 VBlank 时就会产生 NMI 中断
        //0xFFFC, 0xFFFD: RESET 中断地址
        RESET((short) 0xFFFC),//复位中断，RESET 按钮按下后或者系统刚上电时产生
        //0xFFFE, 0xFFFF: IRQ 中断地址
        IRQ((short) 0xFFFE)//可屏蔽中断，如果 P 的 I 标志置 1，则可以屏蔽该中断，同时也可以通过 BRK 指令由软件自行触发
        ;

        public short address;

        InterruptVector(short address) {
            this.address = address;
        }

    }

    /**
     * 标志寄存器的标志位
     */
    private enum Flags {

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

    private class Registers {

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


    /**
     * http://www.oxyron.de/html/opcodes02.html
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


    private class Instruction {

        public Opcode opcode;
        public AddressingMode addressingMode;
        public byte dataBytes;
        public byte cycles;

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
            this.dataBytes =dataBytes;
            this.cycles =cycles;
            this.pageCycles = pageCycles;
        }
    }

    private class CPUBus implements IBus {

        IMapper mapper;

        public CPUBus(IMapper mapper) {
            this.mapper = mapper;
        }


        @Override
        public void writeByte(short address, byte data) {
            if (address < 0x2000) {
                // RAM
                ram[address & 0x07FF] = data;
            } else if (address < 0x6000) {
                // IO Registers, 暂时不实现
            } else {
                // Cartridge
                this.mapper.write(address, data);
            }
        }

        @Override
        public void writeWord(short address, short data) {
            this.writeByte(address, (byte)(data & 0xFF));
            this.writeByte(address, (byte) ((data >> 8) & 0xFF));
        }

        @Override
        public byte readByte(short address) {
            return 0;
        }

        @Override
        public short readWord(short address) {
            return (short) ((this.readByte((short) (address + 1)) << 8 | this.readByte(address)) & 0xFFFF);
        }
    }
}
