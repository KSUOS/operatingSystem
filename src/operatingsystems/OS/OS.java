/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;
import operatingsystems.VM.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class OS {
    private ProgramFileLoader pfl;
    public VM vm;
    
    public ArrayList<Program> programs = new ArrayList<Program>();
    
    private Scheduler scheduler;
    
    public OS(Scheduler scheduler, VM vm) {
	this.scheduler = scheduler;
	this.scheduler.os = this;
	this.vm = vm;
	this.pfl = new ProgramFileLoader(this);
    }
    
    public void load(String filename) throws InterruptedException {
	this.pfl.load(filename);
    }
    
    /**
     * Schedules and runs all programs from the specified filename
     * @throws java.lang.InterruptedException
     */
    public void run() throws InterruptedException, IOException {	
	// Add programs to the scheduler
	for (Program p : this.programs) {
	    System.out.println("Adding program " + p);
	    this.scheduler.addProgram(p);
	}
	
	for (CPU cpu : this.vm.cpus) {
	    cpu.os = this;
	    cpu.start();
	}
	
	this.scheduler.load(); // Load programs into memory after starting the CPUs so they can start working as it loads the programs
	
	for (CPU cpu : this.vm.cpus) {
	    cpu.join();
	}
    }
    
    /**
     * Load a new program into the CPU when ready
     * @param cpu 
     * @throws java.lang.InterruptedException 
     */
    public void schedule(CPU cpu) throws InterruptedException {
	if (!this.scheduler.programsWaiting() && !this.scheduler.programsReady() && !this.scheduler.programsRunning()) {
	    this.shutdown();
	    return;
	}
	this.scheduler.schedule(cpu);
    }
    
    /**
     * Called by the CPU when the CPU has finished processing
     * @param cpu 
     */
    public synchronized void onHalted(CPU cpu) throws InterruptedException, IOException {
	// Save output buffer
	int[] outputBuffer = new int[cpu.currentProgram.outputBufferSize];
	for (int i = 0; i < cpu.currentProgram.outputBufferSize; i++) {
	    outputBuffer[i] = this.vm.mmu.read(cpu.currentProgram, cpu.currentProgram.instructionCount + cpu.currentProgram.inputBufferSize + i);
	}
	this.vm.disk.write(cpu.currentProgram.diskAddress + cpu.currentProgram.instructionCount + cpu.currentProgram.inputBufferSize, outputBuffer);
	
	this.scheduler.haltHandler(cpu);
    }
    
    public synchronized void shutdown() throws InterruptedException {
	this.vm.shutdown();
    }
}