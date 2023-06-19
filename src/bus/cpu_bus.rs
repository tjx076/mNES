
use crate::mapper::IMapper;
use crate::bus::IBus;

/**
 *
 * https://www.nesdev.org/wiki/CPU_memory_map
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

const RAM_SIZE: usize = 2*1024;//byte
const IO_REGISTERS_1_SIZE: usize = 8;
const IO_REGISTERS_2_SIZE: usize = 32;

pub struct CPUBus {
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
     */
    ram: Vec<u8>,//$0000-$0800
    /**
     * 0x2000 - 0x401F ( IO Registers )
     * 这里包含了部分外设的数据，包括 PPU，APU，输入设备的寄存器。比如 CPU 如果想读写 VRAM 的数据，就得靠 PPU 寄存器作为中介
     */
    io_regs_1: Vec<u8>,//$2000-2008
    io_regs_2: Vec<u8>,//$4000-$4020

    mapper: Option<Box<dyn IMapper>>,
}

impl CPUBus {
    pub fn from() -> Self {
        CPUBus {
            ram: Vec::with_capacity(RAM_SIZE),
            io_regs_1: Vec::with_capacity(IO_REGISTERS_1_SIZE),
            io_regs_2: Vec::with_capacity(IO_REGISTERS_2_SIZE),
            mapper: None,
        }
    }

     pub fn set_mapper(&mut self, mapper: Box<dyn IMapper>) {
        self.mapper = Some(mapper);
     }
}

impl IBus for CPUBus {
    fn writeb(&mut self, address: u16, data: u8) {
        if address < 0x2000 {
            // RAM
            self.ram[(address & 0x07FF) as usize] = data;
        } else if address < 0x6000 {
            // IO Registers, 暂时不实现
        } else {
            // mapper
            let mapper = self.mapper.as_mut().unwrap();
            mapper.write(address, data);
        }
    }

    fn writew(&mut self, address: u16, data: u16) {
        self.writeb(address, (data & 0xFF) as u8);
        self.writeb(address + 1, ((data >> 8) & 0xFF) as u8);
    }

    fn readb(&self, address: u16) -> u8 {
        if address < 0x2000 {
            // RAM
            return self.ram[(address & 0x07FF) as usize];
        } else if address < 0x6000 {
            // IO Registers, 暂时不实现
            return 0;
        } else {
            // mapper
            let mapper = self.mapper.as_ref().unwrap();
            return mapper.read(address);
        }
    }

    fn readw(&self, address: u16) -> u16 {
        let low = self.readb(address);
        let high = self.readb(address + 1);
        return ((high << 8) as u16 | low as u16) & 0xFFFF;
    }
}
