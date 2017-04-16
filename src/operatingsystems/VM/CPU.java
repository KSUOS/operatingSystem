/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import operatingsystems.OS.*;

// IO   - (00) OPCODE(6) SREG(4) SREG(4) DREG(4) (000000000000)
// COND - (01) OPCODE(6) BREG(4) DREG(4) Address(16)

class Opcode {
    public static final byte RD = 0x00;
    public static final byte WR = 0x01;
    public static final byte ST = 0x02;
    public static final byte LW = 0x03;
    public static final byte MOV = 0x04;
    public static final byte ADD = 0x05;
    public static final byte SUB = 0x06;
    public static final byte MUL = 0x07;
    public static final byte DIV = 0x08;
    public static final byte AND = 0x09;
    public static final byte OR = 0x0A;
    public static final byte MOVI = 0x0B;
    public static final byte ADDI = 0x0C;
    public static final byte MULTI = 0x0D;
    public static final byte DIVI = 0x0E;
    public static final byte LDI = 0x0F;
    public static final byte SLT = 0x10;
    public static final byte SLTI = 0x11;
    public static final byte HLT = 0x12;
    public static final byte NOP = 0x13;
    public static final byte JMP = 0x14;
    public static final byte BEQ = 0x15;
    public static final byte BNE = 0x16;
    public static final byte BEZ = 0x17;
    public static final byte BNZ = 0x18;
    public static final byte BGZ = 0x19;
    public static final byte BLZ = 0x1A;
};

class Type {
    public static final byte Arithmetic = 0x00;
    public static final byte ConditionalJump = 0x01;
    public static final byte UnconditionalJump = 0x02;
    public static final byte IO = 0x03;
}

/**
 *
 * @author brandon
 */
public class CPU extends Thread {
    public byte cpuId = 0;
    
    /**
     * If CPUState.WAITING, the CPU has nothing to do
     */
    public CPUState state = CPUState.HALTED;
    
    public Program currentProgram = null;
    
    /**
     * Program counter
     */
    private int PC = 0;
    
    /**
     * Instruction register
     */
    private int IR = 0;
    
    private int[] registers = new int[16];
    
    /**
     * Parent driver
     */
    private VM vm = null;
    
    /**
     * Instance of OS for access to the scheduler
     */
    public OS os = null;
    
    /**
     * Initialize a CPU with the driver owner and 0-based CPU identifier
     * @param driver
     * @param cpuId 
     */
    public CPU(VM vm, byte cpuId) {
	this.vm = vm;
	this.cpuId = cpuId;
	this.setName("CPU #" + cpuId);
    }
    
    /**
     * Read a value from memory for the current program
     * @param offset
     * @return 
     */
    private int memoryRead(int offset) throws PageFaultException {
	return this.vm.mmu.read(this.currentProgram, offset / 4);
    }
    
    /**
     * Sets a value in memory for the current program
     * @param offset
     * @return 
     */
    private void memoryWrite(int offset, int value) throws PageFaultException {
	this.vm.mmu.write(this.currentProgram, offset / 4, value);
    }
    
    /**
     * Read the instruction located at the PC into the IR
     */
    private void fetch() throws PageFaultException {
	this.IR = this.vm.mmu.read(this.currentProgram, this.PC);
    }   
    
    /**
     * Fetch instruction, parse, and execute current instruction
     */
    private void execute() throws InterruptedException, PageFaultException {
	// No program to execute
	if (this.currentProgram == null) {
	    this.state = CPUState.HALTED;
	    return;
	}
	
	// Program done
	if (this.PC >= this.currentProgram.instructionCount) {
	    System.out.println("CPU #" + this.cpuId + " - PC tried to go outside of instructionCount. Halting");
	    this.state = CPUState.HALTED;
	    return;
	}
	
	this.fetch();
	
	byte type = (byte)(this.IR >> 30 & 0b11);
	byte opcode = (byte)(this.IR >> 24 & 0b111111);
	
	// stupid java
	byte sReg1, sReg2, dReg, bReg, reg1, reg2;
	int address;
	
	final int fourBits = 0b1111;
	final int sixteenBits = 0b1111111111111111;
	final int twentyFourBits = 0b111111111111111111111111;
	
	switch (type) {
	    case Type.Arithmetic:
		sReg1 = (byte)(this.IR >> 20 & fourBits);
		sReg2 = (byte)(this.IR >> 16 & fourBits);
		dReg  = (byte)(this.IR >> 12 & fourBits);
		
		switch (opcode) {
		    case Opcode.MOV:
			this.registers[sReg1] = this.registers[sReg2];
			break;
		
		    case Opcode.ADD:
			this.registers[dReg] = this.registers[sReg1] + this.registers[sReg2];
			break;
			
		    case Opcode.SUB:
			this.registers[dReg] = this.registers[sReg1] - this.registers[sReg2];
			break;
			
		    case Opcode.MUL:
			this.registers[dReg] = this.registers[sReg1] * this.registers[sReg2];
			break;
		
		    case Opcode.DIV:
			this.registers[dReg] = this.registers[sReg1] / this.registers[sReg2];
			break;
			
		    case Opcode.AND:
			this.registers[dReg] = this.registers[sReg1] & this.registers[sReg2];
			break;
			
		    case Opcode.OR:
			this.registers[dReg] = this.registers[sReg1] | this.registers[sReg2];
			break;
			
		    case Opcode.SLT:
			this.registers[dReg] = (this.registers[sReg1] < this.registers[sReg2] ? 1 : 0);
			break;
			
		    case Opcode.SLTI:
			this.registers[sReg1] = (this.registers[sReg1] < dReg ? 1 : 0);
			break;
		}
		
		break;
		
	    case Type.ConditionalJump:
		bReg    = (byte)(this.IR >> 20 & fourBits);
		dReg    = (byte)(this.IR >> 16 & fourBits);
		address =  (int)(this.IR >> 0  & sixteenBits);
		
		switch (opcode) {
		    case Opcode.ST:
			this.memoryWrite(this.registers[dReg], this.registers[bReg]);
			break;
		    
		    case Opcode.LW:
			this.registers[dReg] = this.memoryRead(this.registers[bReg]);
			break;
			
		    case Opcode.MOVI:
			this.registers[dReg] = address;
			break;
			
		    case Opcode.LDI:
			this.registers[dReg] = address;
			break;
			
		    case Opcode.ADDI:
			this.registers[dReg] += address;
			break;
			
		    case Opcode.MULTI:
			this.registers[dReg] *= address;
			break;
			
		    case Opcode.DIVI:
			this.registers[dReg] /= address;
			break;
			
		    case Opcode.BEQ:
			if (this.registers[bReg] == this.registers[dReg]) {
			    this.PC = address / 4;
			    this.currentProgram.instructionsExecuted++;
			    return;
			}
			break;
			
		    case Opcode.BNE:
			if (this.registers[bReg] != this.registers[dReg]) {
			    this.PC = address / 4;
			    this.currentProgram.instructionsExecuted++;
			    return;
			}
			break;
			
		    case Opcode.BEZ:
			if (this.registers[bReg] == 0) {
			    this.PC = address / 4;
			    this.currentProgram.instructionsExecuted++;
			    return;
			}
			break;
			
		    case Opcode.BNZ:
			if (this.registers[bReg] != 0) {
			    this.PC = address / 4;
			    this.currentProgram.instructionsExecuted++;
			    return;
			}
			break;
			
		    case Opcode.BGZ:
			if (this.registers[bReg] > 0) {
			    this.PC = address / 4;
			    this.currentProgram.instructionsExecuted++;
			    return;
			}
			break;
			
		    case Opcode.BLZ:
			if (this.registers[bReg] < 0) {
			    this.PC = address / 4;
			    this.currentProgram.instructionsExecuted++;
			    return;
			}
			break;
		}
		
		break;
		
	    case Type.UnconditionalJump:
		address =  (int)(this.IR >> 0  & twentyFourBits);
		
		switch (opcode) {
		    case Opcode.JMP:
			this.PC = address / 4;
			break;
			
		    case Opcode.HLT:
			this.state = CPUState.HALTED;
			this.PC = 0;
			this.IR = 0;
			this.currentProgram.instructionsExecuted++;
			return;
		}
		
		break;
		
	    case Type.IO:
		reg1    = (byte)(this.IR >> 20 & fourBits);
		reg2    = (byte)(this.IR >> 16 & fourBits);
		address =  (int)(this.IR >> 0  & sixteenBits);
				
		switch (opcode) {
		    case Opcode.RD:
			if (address != 0) {
			    this.registers[reg1] = this.memoryRead(address);
			}
			else {
			    this.registers[reg1] = this.memoryRead(this.registers[reg2]);
			}
			break;
			
		    case Opcode.WR:
			if (address != 0) {
			    this.memoryWrite(address, this.registers[reg1]);
			}
			else {
			    this.memoryWrite(this.registers[reg2], this.registers[reg1]);
			}
			break;
		}
		
		this.currentProgram.ioOperations++;
		break;
	}
	
	this.currentProgram.instructionsExecuted++;
	this.PC++;
    }
    
    /**
     * The run loop of the CPU
     */
    public void run() {
	while (true) {
	    try {
		if (this.state == CPUState.HALTED) {
		    Accounting.onCPUStateChange(this);
		    
		    if (this.currentProgram != null) {
			// tell scheduler that I have finished
			System.out.println("CPU #" + this.cpuId + " - Finished executing Program " + this.currentProgram.pid);
			this.os.onHalted(this);
		    }

		    // Wait for a new program to run
		    System.out.println("CPU #" + this.cpuId + " - Waiting for scheduler");
		    
		    this.os.schedule(this);
		    if (this.currentProgram == null) break;
		    
		    System.out.println("CPU #" + this.cpuId + " - Program " + this.currentProgram.pid + " scheduled");
		    
		    this.state = CPUState.RUNNING;
		    Accounting.onCPUStateChange(this);
		    
		    this.PC = this.currentProgram.cpuState[0];
		    for (int i = 0; i < 16; i++) {
			this.registers[i] = this.currentProgram.cpuState[i + 1];
		    }
		}
		else {
		    this.execute();
		}
	    }
	    catch (InterruptedException e) {
		System.out.println("Got interupt");
		if (this.vm.shuttingDown) break;
	    } catch (IOException ex) {
		System.err.println(ex.toString());
	    } catch (PageFaultException ex) {
		// Save CPU state into program
		this.currentProgram.cpuState[0] = this.PC;
		for (int i = 0; i < 16; i++) {
		    this.currentProgram.cpuState[i + 1] = this.registers[i];
		}
		
		// Alert OS of page fault
		this.os.onFault(this);
	    }
	}
    }
    
    public String toString() {
	return "CPU #" + this.cpuId;
    }
}
