package org.wrpg.mnes.cpu;

import org.wrpg.mnes.IBus;
import org.wrpg.mnes.IMapper;


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
public class CPUBus implements IBus {

    private static final int RAM_SIZE = 2*1024;//byte

    private static final int RAM_ZERO_PAGE = 256;//byte
    private static final int RAM_STACK = 256;//byte
    private static final int RAM_OTHER = RAM_SIZE - RAM_ZERO_PAGE - RAM_STACK;//byte

    private static final int IO_REGISTERS_1_SIZE = 8;//byte
    private static final int IO_REGISTERS_2_SIZE = 32;//byte

    /**
     * 主机中 2KB RAM
     * 用于游戏运行数据存储
     *
     * 这是主机中 2KB RAM 的数据，分成了 3 块
     *
     * 0x0000 - 0x00FF ( Zero page )
     * 前 256 字节划分为 Zero page，这块内存相比其他区域不同点在于能让 CPU 以更快的速度访问，所以需要频繁读写的数据会优先放入此区域
     * 0x0100 - 0x01FF ( Stack )
     * 这一块区域用于栈数据的存储，SP（栈指针） 从 0x1FF 处向下增长
     * 0x0200 - 0x07FF ( 剩余 RAM )
     * 这是 2KB 被 Zero page 和 Sack 瓜分后剩余的区域
     *
     */
    private byte[] ram = new byte[RAM_SIZE];//$0000-$0800

    /**
     * 0x0800 - 0x2000 ( Mirrors )
     * 你可能会感觉到奇怪这个 Mirror 到底是干什么的。实际上它是 0x0000 - 0x07FF 数据的镜像，总共重复 3 次
     * 例如：0x0001, 0x0801, 0x1001, 0x1801 都指向了同样的数据，用程序来解释的话，就是：
     * address &= 0x07FF
     * 对应到硬件上的话，就是 bit11 - 13 的线不接
     * 至于为什么任天堂要这样设计？我猜可能是考虑到成本原因，2KB RAM 够用了，不需要更大的 RAM，但是地址空间得用完啊，所以才有了 Mirror 效果
     *
     */
//    private byte[] mirror1 = new byte[RAM_SIZE];
//    private byte[] mirror2 = new byte[RAM_SIZE];
//    private byte[] mirror3 = new byte[RAM_SIZE];

    /**
     * 0x2000 - 0x401F ( IO Registers )
     * 这里包含了部分外设的数据，包括 PPU，APU，输入设备的寄存器。比如 CPU 如果想读写 VRAM 的数据，就得靠 PPU 寄存器作为中介
     *
     */
    private byte[] ioRegisters1 = new byte[IO_REGISTERS_1_SIZE];//$2000-2008
    private byte[] ioRegisters2 = new byte[IO_REGISTERS_2_SIZE];//$4000-$4020

    IMapper mapper;

    public CPUBus(IMapper mapper) {
        this.mapper = mapper;
    }


    @Override
    public void writeByte(short address, byte data) {
        if (address < 0x2000) {
            // RAM
            this.ram[address & 0x07FF] = data;
        } else if (address < 0x6000) {
            // IO Registers, 暂时不实现
        } else {
            // Cartridge
            this.mapper.write(address, data);
        }
    }

    @Override
    public void writeWord(short address, short data) {
        this.writeByte(address, (byte) (data & 0xFF));
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
