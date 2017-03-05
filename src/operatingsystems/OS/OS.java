/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.util.*;
import operatingsystems.VM.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class OS {
    public ProgramFileLoader pfl;
    public VM vm;
    
    public ArrayList<Program> programs = new ArrayList<Program>();
    
    public ArrayDeque<Program> readyQueue = new ArrayDeque<Program>();
    public ArrayDeque<Program> waitingQueue = new ArrayDeque<Program>();
    public ArrayDeque<Program> doneQueue = new ArrayDeque<Program>();
    
    public Scheduler scheduler;
    
    public int usedMemory = 0;
    
    
    public OS(Scheduler scheduler, VM vm) {
	this.scheduler = scheduler;
	this.vm = vm;
	this.pfl = new ProgramFileLoader(vm);
    }
    
    /**
     * Loads, schedules, and runs all programs
     */
    public void run(String filename) throws InterruptedException {
	// Load programs onto disk
	this.pfl.load(filename);
	
	// Schedule programs
	this.scheduler.schedule();
	
	ArrayList<CPUThread> cpuThreads = new ArrayList<CPUThread>();
	// Create CPU execution threads
	for (CPU cpu : this.vm.cpus) {
	    CPUThread t = new CPUThread(this, cpu);
	    cpuThreads.add(t);
	    t.start();
	}
	
	for (CPUThread t : cpuThreads) {
	    t.join();
	}
    }
}

class CPUThread extends Thread {
    
    private CPU cpu;
    private OS os;
    public CPUThread(OS os, CPU cpu) {
	this.cpu = cpu;
	this.os = os;
    }
    
    public void run() {
	while (true) {
	    if (this.cpu.state == CPUState.WAITING) {
		if (this.cpu.currentProgram != null) {
		    //unload from memory
		    this.os.doneQueue.add(this.cpu.currentProgram);
		}
		
		this.os.scheduler.schedule();
		
		try {
		    this.cpu.currentProgram = this.os.readyQueue.pop();
		}
		catch (NoSuchElementException e) {
		    return;
		}
		
		//Load program into memory
		this.cpu.currentProgram.memoryAddress = this.os.usedMemory + 1;
		this.os.usedMemory += this.cpu.currentProgram.memoryAddress;
		
		int[] program = this.os.vm.disk.read(this.cpu.currentProgram.diskAddress, this.cpu.currentProgram.instructionCount);
		this.os.vm.mmu.write(this.cpu.currentProgram, 0, program);
		
		this.cpu.state = CPUState.EXECUTING;
	    }
	    
	    cpu.execute();
	}
    }
}
