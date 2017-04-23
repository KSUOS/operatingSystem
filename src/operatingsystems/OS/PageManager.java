/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.*;
import operatingsystems.VM.RAM;

/**
 *
 * @author brandon
 */
public class PageManager {
    private final OS os;
    
    private final int pageSize;
    
    private int numPages;
    private Semaphore pageLock = new Semaphore(1);
    
    public int usedPages = 0;
    public ArrayList<Boolean> pageStatus = new ArrayList<>();
    
    public PageManager(OS os) {
	this.os = os;
	this.pageSize = this.os.vm.mmu.pageSize;
	
	if (this.pageSize == 0) return;
	
	this.numPages = RAM.LENGTH / this.pageSize;
	for (int i = 0; i < this.numPages; i++) {
	    this.pageStatus.add(Boolean.FALSE);
	}
    }
    
    /**
     * Loads a page from disk into memory, returning true if successful or false if there are no frames available to load into
     * @param program
     * @param address
     * @return
     * @throws InterruptedException
     * @throws IOException 
     */
    boolean loadPage(Program program, int address) throws InterruptedException, IOException {
	this.pageLock.acquire();
	int freePageAddress = this.takeNextFreePage();
	if (freePageAddress == -1) {
	    this.pageLock.release();
	    return false;
	}
	
	int page = address / this.pageSize;
	
	PageTableEntry pte = new PageTableEntry();
	pte.page = page;
	pte.physicalAddress = freePageAddress;
	
	program.pageTable.add(pte);
	
	System.out.println("Done loading page " + page + " of " + program.toString() + " into address " + freePageAddress);
	
	int[] data = this.os.vm.disk.read(program.diskAddress + (page * this.pageSize), pageSize);
	for (int i = 0; i < this.pageSize; i++) {
	    this.os.vm.mmu.ram.write(pte.physicalAddress + i, data[i]);
	}
	
	this.usedPages++;
	Accounting.onPageUsageChange(this.usedPages);
	
	this.pageLock.release();
	return true;
    }
    
    void freeProgram(Program program) throws IOException, InterruptedException {
	System.out.println("Freeing " + program.toString());
	for (PageTableEntry pte : program.pageTable) {
	    if (pte.dirty) {
		int[] data = new int[this.pageSize];
		for (int i = 0; i < this.pageSize; i++) {
		    data[i] = this.os.vm.mmu.ram.read(pte.physicalAddress + i);
		}
		
		this.os.vm.disk.write(program.diskAddress + (pte.page * this.pageSize), data);
	    }
	    
	    this.pageLock.acquire();
	    this.pageStatus.set(pte.physicalAddress / this.pageSize, Boolean.FALSE);
	    
	    this.usedPages--;
	    Accounting.onPageUsageChange(this.usedPages);
	    
	    this.pageLock.release();
	}
	
	if (this.pageSize == 0) {
	    int[] outputBuffer = new int[program.outputBufferSize];
	    for (int i = 0; i < program.outputBufferSize; i++) {
		outputBuffer[i] = this.os.vm.mmu.ram.read(program.memoryAddress + program.instructionCount + program.inputBufferSize + i);
	    }
	    this.os.vm.disk.write(program.diskAddress + program.instructionCount + program.inputBufferSize, outputBuffer);
	}
	
	program.pageTable.clear();
    }
    
    /**
     * Returns the address of the next page or -1 if there is no room left
     * @return address of the next page available
     */
    private int takeNextFreePage() {
	for(int i = 0; i < this.numPages; i++) {
	    Boolean taken = this.pageStatus.get(i);
	    if(taken.booleanValue() == false) {
		this.pageStatus.set(i, Boolean.TRUE);
		return i * pageSize;
	    }
	}
	return -1;
    }
}
