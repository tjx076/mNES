
use crate::cpu::CPU;
use crate::cpu::addr_mode::AddressData;

use std::collections::HashMap;

/**
 * CPU 指令
 */
#[derive(Hash, Eq, PartialEq, Debug)]
pub enum Instruction {
    //Logical and arithmetic commands:
    // Opcode	imp	imm	zp	zpx	zpy	izx	izy	abs	abx	aby	ind	rel	Function	N	V	B	D	I	Z	C
    // ORA	 	$09	$05	$15	 	$01	$11	$0D	$1D	$19	 	 	A:=A or {adr}	*	 	 	 	 	*	 
    // AND	 	$29	$25	$35	 	$21	$31	$2D	$3D	$39	 	 	A:=A&{adr}	*	 	 	 	 	*	 
    // EOR	 	$49	$45	$55	 	$41	$51	$4D	$5D	$59	 	 	A:=A exor {adr}	*	 	 	 	 	*	 
    // ADC	 	$69	$65	$75	 	$61	$71	$6D	$7D	$79	 	 	A:=A+{adr}	*	*	 	 	 	*	*
    // SBC	 	$E9	$E5	$F5	 	$E1	$F1	$ED	$FD	$F9	 	 	A:=A-{adr}	*	*	 	 	 	*	*
    // CMP	 	$C9	$C5	$D5	 	$C1	$D1	$CD	$DD	$D9	 	 	A-{adr}	*	 	 	 	 	*	*
    // CPX	 	$E0	$E4	 	 	 	 	$EC	 	 	 	 	X-{adr}	*	 	 	 	 	*	*
    // CPY	 	$C0	$C4	 	 	 	 	$CC	 	 	 	 	Y-{adr}	*	 	 	 	 	*	*
    // DEC	 	 	$C6	$D6	 	 	 	$CE	$DE	 	 	 	{adr}:={adr}-1	*	 	 	 	 	*	 
    // DEX	$CA	 	 	 	 	 	 	 	 	 	 	 	X:=X-1	*	 	 	 	 	*	 
    // DEY	$88	 	 	 	 	 	 	 	 	 	 	 	Y:=Y-1	*	 	 	 	 	*	 
    // INC	 	 	$E6	$F6	 	 	 	$EE	$FE	 	 	 	{adr}:={adr}+1	*	 	 	 	 	*	 
    // INX	$E8	 	 	 	 	 	 	 	 	 	 	 	X:=X+1	*	 	 	 	 	*	 
    // INY	$C8	 	 	 	 	 	 	 	 	 	 	 	Y:=Y+1	*	 	 	 	 	*	 
    // ASL	$0A	 	$06	$16	 	 	 	$0E	$1E	 	 	 	{adr}:={adr}*2	*	 	 	 	 	*	*
    // ROL	$2A	 	$26	$36	 	 	 	$2E	$3E	 	 	 	{adr}:={adr}*2+C	*	 	 	 	 	*	*
    // LSR	$4A	 	$46	$56	 	 	 	$4E	$5E	 	 	 	{adr}:={adr}/2	*	 	 	 	 	*	*
    // ROR	$6A	 	$66	$76	 	 	 	$6E	$7E	 	 	 	{adr}:={adr}/2+C*128	*	 	 	 	 	*	*
    ORA, AND, EOR, ADC, SBC, CMP, CPX, CPY, DEC, DEX, DEY, INC, INX, INY, ASL, ROL, LSR, ROR,

    //Move commands:
    // Opcode	imp	imm	zp	zpx	zpy	izx	izy	abs	abx	aby	ind	rel	Function	N	V	B	D	I	Z	C
    // LDA	 	$A9	$A5	$B5	 	$A1	$B1	$AD	$BD	$B9	 	 	A:={adr}	*	 	 	 	 	*	 
    // STA	 	 	$85	$95	 	$81	$91	$8D	$9D	$99	 	 	{adr}:=A	 	 	 	 	 	 	 
    // LDX	 	$A2	$A6	 	$B6	 	 	$AE	 	$BE	 	 	X:={adr}	*	 	 	 	 	*	 
    // STX	 	 	$86	 	$96	 	 	$8E	 	 	 	 	{adr}:=X	 	 	 	 	 	 	 
    // LDY	 	$A0	$A4	$B4	 	 	 	$AC	$BC	 	 	 	Y:={adr}	*	 	 	 	 	*	 
    // STY	 	 	$84	$94	 	 	 	$8C	 	 	 	 	{adr}:=Y	 	 	 	 	 	 	 
    // TAX	$AA	 	 	 	 	 	 	 	 	 	 	 	X:=A	*	 	 	 	 	*	 
    // TXA	$8A	 	 	 	 	 	 	 	 	 	 	 	A:=X	*	 	 	 	 	*	 
    // TAY	$A8	 	 	 	 	 	 	 	 	 	 	 	Y:=A	*	 	 	 	 	*	 
    // TYA	$98	 	 	 	 	 	 	 	 	 	 	 	A:=Y	*	 	 	 	 	*	 
    // TSX	$BA	 	 	 	 	 	 	 	 	 	 	 	X:=S	*	 	 	 	 	*	 
    // TXS	$9A	 	 	 	 	 	 	 	 	 	 	 	S:=X	 	 	 	 	 	 	 
    // PLA	$68	 	 	 	 	 	 	 	 	 	 	 	A:=+(S)	*	 	 	 	 	*	 
    // PHA	$48	 	 	 	 	 	 	 	 	 	 	 	(S)-:=A	 	 	 	 	 	 	 
    // PLP	$28	 	 	 	 	 	 	 	 	 	 	 	P:=+(S)	*	*	 	*	*	*	*
    // PHP	$08	 	 	 	 	 	 	 	 	 	 	 	(S)-:=P	 	 	 	 	 	 	 
    LDA, STA, LDX, STX, LDY, STY, TAX, TXA, TAY, TYA, TSX, TXS, PLA, PHA, PLP, PHP,

    //Jump/Flag commands:
    // Opcode	imp	imm	zp	zpx	zpy	izx	izy	abs	abx	aby	ind	rel	Function	N	V	B	D	I	Z	C
    // BPL	 	 	 	 	 	 	 	 	 	 	 	$10	branch on N=0	 	 	 	 	 	 	 
    // BMI	 	 	 	 	 	 	 	 	 	 	 	$30	branch on N=1	 	 	 	 	 	 	 
    // BVC	 	 	 	 	 	 	 	 	 	 	 	$50	branch on V=0	 	 	 	 	 	 	 
    // BVS	 	 	 	 	 	 	 	 	 	 	 	$70	branch on V=1	 	 	 	 	 	 	 
    // BCC	 	 	 	 	 	 	 	 	 	 	 	$90	branch on C=0	 	 	 	 	 	 	 
    // BCS	 	 	 	 	 	 	 	 	 	 	 	$B0	branch on C=1	 	 	 	 	 	 	 
    // BNE	 	 	 	 	 	 	 	 	 	 	 	$D0	branch on Z=0	 	 	 	 	 	 	 
    // BEQ	 	 	 	 	 	 	 	 	 	 	 	$F0	branch on Z=1	 	 	 	 	 	 	 
    // BRK	$00	 	 	 	 	 	 	 	 	 	 	 	(S)-:=PC,P PC:=($FFFE)	 	 	1	 	1	 	 
    // RTI	$40	 	 	 	 	 	 	 	 	 	 	 	P,PC:=+(S)	*	*	 	*	*	*	*
    // JSR	 	 	 	 	 	 	 	$20	 	 	 	 	(S)-:=PC PC:={adr}	 	 	 	 	 	 	 
    // RTS	$60	 	 	 	 	 	 	 	 	 	 	 	PC:=+(S)	 	 	 	 	 	 	 
    // JMP	 	 	 	 	 	 	 	$4C	 	 	$6C	 	PC:={adr}	 	 	 	 	 	 	 
    // BIT	 	 	$24	 	 	 	 	$2C	 	 	 	 	N:=b7 V:=b6 Z:=A&{adr}	*	*	 	 	 	*	 
    // CLC	$18	 	 	 	 	 	 	 	 	 	 	 	C:=0	 	 	 	 	 	 	0
    // SEC	$38	 	 	 	 	 	 	 	 	 	 	 	C:=1	 	 	 	 	 	 	1
    // CLD	$D8	 	 	 	 	 	 	 	 	 	 	 	D:=0	 	 	 	0	 	 	 
    // SED	$F8	 	 	 	 	 	 	 	 	 	 	 	D:=1	 	 	 	1	 	 	 
    // CLI	$58	 	 	 	 	 	 	 	 	 	 	 	I:=0	 	 	 	 	0	 	 
    // SEI	$78	 	 	 	 	 	 	 	 	 	 	 	I:=1	 	 	 	 	1	 	 
    // CLV	$B8	 	 	 	 	 	 	 	 	 	 	 	V:=0	 	0	 	 	 	 	 
    // NOP	$EA	 	 	 	 	 	 	 	 	 	 	 	 	 	 	 	 	 	 	 
    BPL, BMI, BVC, BVS, BCC, BCS, BNE, BEQ, BRK, RTI, JSR, RTS, JMP, BIT, CLC, SEC, CLD, SED, CLI, SEI, CLV, NOP,

    // Illegal opcodes:
    // Opcode	imp	imm	zp	zpx	zpy	izx	izy	abs	abx	aby	ind	rel	Function	N	V	B	D	I	Z	C
    // SLO	 	 	$07	$17	 	$03	$13	$0F	$1F	$1B	 	 	{adr}:={adr}*2 A:=A or {adr}	*	 	 	 	 	*	*
    // RLA	 	 	$27	$37	 	$23	$33	$2F	$3F	$3B	 	 	{adr}:={adr}rol A:=A and {adr}	*	 	 	 	 	*	*
    // SRE	 	 	$47	$57	 	$43	$53	$4F	$5F	$5B	 	 	{adr}:={adr}/2 A:=A exor {adr}	*	 	 	 	 	*	*
    // RRA	 	 	$67	$77	 	$63	$73	$6F	$7F	$7B	 	 	{adr}:={adr}ror A:=A adc {adr}	*	*	 	 	 	*	*
    // SAX	 	 	$87	 	$97	$83	 	$8F	 	 	 	 	{adr}:=A&X	 	 	 	 	 	 	 
    // LAX	 	 	$A7	 	$B7	$A3	$B3	$AF	 	$BF	 	 	A,X:={adr}	*	 	 	 	 	*	 
    // DCP	 	 	$C7	$D7	 	$C3	$D3	$CF	$DF	$DB	 	 	{adr}:={adr}-1 A-{adr}	*	 	 	 	 	*	*
    // ISC	 	 	$E7	$F7	 	$E3	$F3	$EF	$FF	$FB	 	 	{adr}:={adr}+1 A:=A-{adr}	*	*	 	 	 	*	*
    DCP, ISC, LAX, RLA, RRA, SAX, SLO, SRE,
}

type InstFn = fn(&mut CPU, AddressData);

lazy_static! {
    static ref INSTRUCTION_MAP: HashMap<Instruction, InstFn> = {
        let mut m = HashMap::new();
        m.insert(Instruction::ORA, ora as InstFn);
        m.insert(Instruction::AND, and);
        m
    };
}

fn ora(cpu: &mut CPU, ad: AddressData) {

}

fn and(cpu: &mut CPU, ad: AddressData) {

}
