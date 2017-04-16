/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import operatingsystems.OS.PageFaultException;
import operatingsystems.OS.PageTableEntry;
import operatingsystems.OS.Program;

/**
 * Controls writes to RAM. Actual element in computers used for paging (not needed in this project, but good to implement)
 * @author brandon
 */
public class MMU {
    
    public RAM ram;
    public int pageSize;
    
    private VM vm;
    public MMU(VM vm, int pageSize) {
	this.vm = vm;
	this.pageSize = pageSize;
	this.ram = new RAM(vm);
    }
    
    /**
     * Reads a word from a program's RAM
     * @param program
     * @param offset
     * @return 
     */
    public int read(Program program, int offset) throws PageFaultException {
	if (this.pageSize == 0) {
	    return this.ram.read(program.memoryAddress + offset);
	}
	else {
	    int off = offset % this.pageSize;
	    int page = Math.floorDiv(offset, this.pageSize);
	    
	    for (PageTableEntry pte : program.pageTable) {
		if (pte.page == page) {
		    return this.ram.read(pte.physicalAddress + off);
		}
	    }
	    program.faultAddress = offset;
	    System.out.println(program.toString() + " page faulted on read @ " + offset);
	    throw new PageFaultException();
	}
    }
    
    /**
     * Writes a word to a program's RAM
     * @param program
     * @param offset
     * @param value 
     */
    public void write(Program program, int offset, int value) throws PageFaultException {
	if (this.pageSize == 0) {
	    this.ram.write(program.memoryAddress + offset, value);
	}
	else {
	    int off = offset % this.pageSize;
	    int page = Math.floorDiv(offset, this.pageSize);
	    
	    for (PageTableEntry pte : program.pageTable) {
		if (pte.page == page) {
		    pte.dirty = true;
		    this.ram.write(pte.physicalAddress + off, value);
		    return;
		}
	    }
	    program.faultAddress = offset;
	    System.out.println(program.toString() + " page faulted on write @ " + offset);
	    throw new PageFaultException();
	}
    }
    
    /**
     * Writes words to a program's RAM
     * @param program
     * @param offset
     * @param value s
     */
    public void write(Program program, int offset, int[] values) throws PageFaultException {
	for (int i = 0; i < values.length; i++) {
	    this.write(program, offset + i, values[i]);
	}
    }
}
