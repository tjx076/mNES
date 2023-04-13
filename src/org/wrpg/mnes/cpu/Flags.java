package org.wrpg.mnes.cpu;

/**
 * 标志寄存器的标志位
 */
enum Flags {
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
