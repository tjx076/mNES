
/** 
 * http://www.oxyron.de/html/opcodes02.html
 * 里面包含了 56 种官方指令和一些非官方指令
 * 每个方块包含了指令，寻址模式，执行周期，
 * 例如： LDY imm 2
 * 表示：LDY 指令，立即寻址，执行完需要 2 个 CPU 时钟
 * 有些方块执行周期后面有个 * 号，这说明该指令在某些情况下需要额外增加 1 ~ 2 个时钟才能完成
 * "*" : add 1 cycle if page boundary is crossed.
 *       add 1 cycle on branches if taken.
 */
pub struct OpCode {
    
}
