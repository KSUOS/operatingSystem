/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import operatingsystems.OS.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class CPU {
    public byte cpuId = 0;
    public CPUState state = CPUState.WAITING;
    
    public Program currentProgram = null;
    
    private int[] registers = new int[16];
    
    /**
     * Program counter
     */
    private int PC = 0;
    
    /**
     * Instruction register
     */
    private int IR = 0;
    
    /**
     * Parent driver
     */
    private VM vm = null;
    
    /**
     * Initialize a CPU with the driver owner and 0-based CPU identifier
     * @param driver
     * @param cpuId 
     */
    public CPU(VM vm, byte cpuId) {
	this.vm = vm;
	this.cpuId = cpuId;
    }
    
    /**
     * Read a value from memory for the current program
     * @param offset
     * @return 
     */
    private int memoryRead(int offset) {
	throw new NotImplementedException();
    }
    
    /**
     * Sets a value in memory for the current program
     * @param offset
     * @return 
     */
    private int memoryRead(int offset, int value) {
	throw new NotImplementedException();
    }
    
    /**
     * Read the instruction located at the PC into the IR
     */
    private void fetch() {
	throw new NotImplementedException();
    }
    
    /**
     * Fetch instruction, parse, and execute current instruction
     */
    public void execute() {
	throw new NotImplementedException();
    }
}
