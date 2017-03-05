/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import operatingsystems.OS.Program;

/**
 * Controls writes to RAM. Actual element in computers used for paging (not needed in this project, but good to implement)
 * @author brandon
 */
public class MMU {
    
    private RAM ram;
    
    private VM vm;
    public MMU(VM vm) {
	this.vm = vm;
	this.ram = new RAM(vm);
    }
    
    /**
     * Reads a word from a program's RAM
     * @param program
     * @param offset
     * @return 
     */
    public int read(Program program, int offset) {
	return this.ram.read(program.memoryAddress + offset);
    }
    
    /**
     * Writes a word to a program's RAM
     * @param program
     * @param offset
     * @param value 
     */
    public void write(Program program, int offset, int value) {
	this.ram.write(program.memoryAddress + offset, value);
    }
    
    /**
     * Writes words to a program's RAM
     * @param program
     * @param offset
     * @param value s
     */
    public void write(Program program, int offset, int[] values) {
	for (int i = 0; i < values.length; i++) {
	    this.ram.write(program.memoryAddress + offset + i, values[i]);
	}
    }
}
