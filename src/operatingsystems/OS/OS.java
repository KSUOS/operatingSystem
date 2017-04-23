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
    private PageManager pageManager;
    
    public ArrayList<Program> programs = new ArrayList<Program>();

    private ArrayList<Program> ioWaitQueue = new ArrayList<Program>();
    
    private Scheduler scheduler;
    
    public OS(Scheduler scheduler, VM vm) {
	this.scheduler = scheduler;
	this.scheduler.os = this;
	this.vm = vm;
	this.pfl = new ProgramFileLoader(this);
	this.pageManager = new PageManager(this);
    }
    
    public void load(String filename) throws InterruptedException {
	this.pfl.load(filename);
    }
    
    /**
     * Schedules and runs all programs from the specified filename
     * @throws java.lang.InterruptedException
     */
    public void run() throws InterruptedException, IOException, PageFaultException {	
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
    public void schedule(CPU cpu) throws InterruptedException, IOException {
	if (!this.scheduler.programsWaiting() && !this.scheduler.programsReady() && !this.scheduler.programsRunning()) {
	    this.shutdown();
	    return;
	}
	
	this.servicePageFaults();
	this.scheduler.schedule(cpu);
    }
    
    /**
     * Called by the CPU when the CPU page faulted
     * @param cpu 
     */
    public void onFault(CPU cpu) {
	Program program = cpu.currentProgram;
	
	program.pageFaults++;
	
	System.out.println("Adding program " + program.pid + " to the iowait queue");
	
	synchronized (this.ioWaitQueue) {
	    this.ioWaitQueue.add(program);
	}
	
	cpu.state = CPUState.HALTED;
	Accounting.onCPUStateChange(cpu);
	cpu.currentProgram = null;
    }
    
    private void servicePageFaults() throws InterruptedException, IOException {
	synchronized (this.ioWaitQueue) {
	    System.out.println("Servicing " + this.ioWaitQueue.size() + " page faults");
	    ArrayList<Program> toRemove = new ArrayList<Program>();
	    for (Program program : this.ioWaitQueue) {
		boolean successfulLoad = this.pageManager.loadPage(program, program.faultAddress);
		if (successfulLoad) {
		    this.scheduler.wait(program);
		    toRemove.add(program);
		}
	    }

	    for (Program program : toRemove) {
		this.ioWaitQueue.remove(program);
	    }
	}
    }
    
    /**
     * Called by the CPU when the CPU has finished processing
     * @param cpu 
     */
    public synchronized void onHalted(CPU cpu) throws InterruptedException, IOException, PageFaultException {	
	this.scheduler.halt(cpu.currentProgram);
	this.pageManager.freeProgram(cpu.currentProgram);
	cpu.currentProgram = null;
	cpu.state = CPUState.HALTED;
	Accounting.onCPUStateChange(cpu);
    }
    
    public synchronized void shutdown() throws InterruptedException {
	this.vm.shutdown();
    }
}