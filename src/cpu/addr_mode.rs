
use crate::cpu::CPU;
use crate::bus::IBus;

//寻址模式
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

type AddrModeFn = Box<dyn FnMut(&mut CPU) -> AddressData + Sync>;

pub enum AddressingMode {
    IMPLICIT(AddrModeFn), //imp
    ACCUMULATOR(AddrModeFn), //
    IMMEDIATE(AddrModeFn), //imm = #$00
    ZERO_PAGE(AddrModeFn), //zp = $00
    ZERO_PAGE_X(AddrModeFn), //zpx = $00,X
    ZERO_PAGE_Y(AddrModeFn), //zpy = $00,Y
    RELATIVE(AddrModeFn), //rel = $0000 (PC-relative)
    ABSOLUTE(AddrModeFn), //abs = $0000
    ABSOLUTE_X(AddrModeFn), //abx = $0000,X
    ABSOLUTE_Y(AddrModeFn), //aby = $0000,Y
    INDIRECT(AddrModeFn), //ind = ($0000)
    X_INDEXED_INDIRECT(AddrModeFn), //izx = ($00,X)
    INDIRECT_Y_INDEXED(AddrModeFn), //izy = ($00),Y
}

pub struct AddressData {
    address: Option<u16>, // Set value to None if immediate mode
    data: Option<u8>, // Set value to None if not immediate mode
    cross_page: bool,
}

lazy_static! {
    static ref ADDRESSING_MODE_MAP: Vec<AddressingMode> = {
        let mut v = Vec::new();
        v.push(AddressingMode::IMPLICIT(Box::new(implicit)));
        v.push(AddressingMode::ACCUMULATOR(Box::new(accumulator)));
        v.push(AddressingMode::IMMEDIATE(Box::new(immediate)));
        v.push(AddressingMode::ZERO_PAGE(Box::new(zero_page)));
        v.push(AddressingMode::ZERO_PAGE_X(Box::new(zero_page_x)));
        v.push(AddressingMode::ZERO_PAGE_Y(Box::new(zero_page_y)));
        v.push(AddressingMode::RELATIVE(Box::new(relative)));
        v.push(AddressingMode::ABSOLUTE(Box::new(absolute)));
        v.push(AddressingMode::ABSOLUTE_X(Box::new(absolute_x)));
        v.push(AddressingMode::ABSOLUTE_Y(Box::new(absolute_y)));
        v.push(AddressingMode::INDIRECT(Box::new(indirect)));
        v.push(AddressingMode::X_INDEXED_INDIRECT(Box::new(x_indexed_indirect)));
        v.push(AddressingMode::INDIRECT_Y_INDEXED(Box::new(indirect_y_indexed)));
        v
    };
}

fn implicit(cpu: &mut CPU) -> AddressData {
    AddressData {
        address: None,
        data: None,
        cross_page: false,
    }
}

fn accumulator(cpu: &mut CPU) -> AddressData {
    AddressData {
      address: None,
      data: Some(cpu.regs.A),
      cross_page: false,
    }
}

fn immediate(cpu: &mut CPU) -> AddressData {
    let data = cpu.bus.readb(cpu.regs.PC);
    cpu.regs.PC += 1;

    AddressData {
      address: None,
      data: Some(data),
      cross_page: false,
    }
}


fn zero_page(cpu: &mut CPU) -> AddressData {
    let address = cpu.bus.readb(cpu.regs.PC);
    cpu.regs.PC += 1;

    AddressData {
      address: Some(address as u16 & 0xFFFF),
      data: None,
      cross_page: false,
    }
}

fn zero_page_x(cpu: &mut CPU) -> AddressData {
    let address = (cpu.bus.readb(cpu.regs.PC) + cpu.regs.X) & 0xFF;
    cpu.regs.PC += 1;

    AddressData {
      address: Some(address as u16 & 0xFFFF),
      data: None,
      cross_page: false,
    }
}

fn zero_page_y(cpu: &mut CPU) -> AddressData {
    let address = (cpu.bus.readb(cpu.regs.PC) + cpu.regs.Y) & 0xFF;
    cpu.regs.PC += 1;

    AddressData {
      address: Some(address as u16 & 0xFFFF),
      data: None,
      cross_page: false,
    }
}

fn relative(cpu: &mut CPU) -> AddressData {
    // Range is -128 ~ 127
    let offset = cpu.bus.readb(cpu.regs.PC);
    cpu.regs.PC += 1;

    let mut address : u16 = 0;
    if (offset & 0x80) != 0 {
        address = cpu.regs.PC + (offset as u16 - 0x100);
    } else {
        address = cpu.regs.PC + offset as u16;
    }

    AddressData {
      address: Some(address & 0xFFFF),
      data: None,
      cross_page: false,
    }
}


fn absolute(cpu: &mut CPU) -> AddressData {
    let address = cpu.bus.readw(cpu.regs.PC);
    cpu.regs.PC += 2;

    AddressData {
      address: Some(address & 0xFFFF),
      data: None,
      cross_page: false,
    }
}

fn absolute_x(cpu: &mut CPU) -> AddressData {
    let base_addr = cpu.bus.readw(cpu.regs.PC);
    cpu.regs.PC += 2;

    let address = base_addr + cpu.regs.X as u16;

    AddressData {
      address: Some(address & 0xFFFF),
      data: None,
      cross_page: is_cross_page(base_addr, address),
    }
}

fn absolute_y(cpu: &mut CPU) -> AddressData {
    let base_addr = cpu.bus.readw(cpu.regs.PC);
    cpu.regs.PC += 2;
    let address = base_addr + cpu.regs.Y as u16;

    AddressData {
      address: Some(address & 0xFFFF),
      data: None,
      cross_page: is_cross_page(base_addr, address),
    }
}

fn indirect(cpu: &mut CPU) -> AddressData {
  let mut address = cpu.bus.readw(cpu.regs.PC);
  cpu.regs.PC += 2;

  if (address & 0xFF) == 0xFF { // Hardware bug
    address = (cpu.bus.readb(address & 0xFF00) << 8) as u16 | cpu.bus.readb(address) as u16;
  } else {
    address = cpu.bus.readw(address);
  }

  AddressData {
    address: Some(address & 0xFFFF),
    data: None,
    cross_page: false,
  }
}

fn x_indexed_indirect(cpu: &mut CPU) -> AddressData {
  let address = cpu.bus.readb(cpu.regs.PC);
  cpu.regs.PC += 1;

  let address = address + cpu.regs.X;

  let l = cpu.bus.readb(address as u16 & 0xFF);
  let h = cpu.bus.readb((address + 1) as u16 & 0xFF);

  AddressData {
    address: Some(((h << 8) as u16 | l as u16) & 0xFFFF),
    data: None,
    cross_page: false,
  }
}

fn indirect_y_indexed(cpu: &mut CPU) -> AddressData {
  let address = cpu.bus.readb(cpu.regs.PC);
  cpu.regs.PC += 1;

  let l = cpu.bus.readb(address as u16 & 0xFF);
  let h = cpu.bus.readb((address + 1) as u16 & 0xFF);

  let base_addr = (h << 8) as u16 | l as u16;
  let address = base_addr + cpu.regs.Y as u16;

  AddressData {
    address: Some(address & 0xFFFF),
    data: None,
    cross_page: is_cross_page(base_addr, address),
  }
}

fn is_cross_page(addr1: u16, addr2: u16) -> bool {
  (addr1 & 0xFF00) != (addr2 & 0xFF00)
}