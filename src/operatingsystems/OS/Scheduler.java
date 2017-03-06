/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.util.ArrayDeque;
import java.util.concurrent.Semaphore;
import operatingsystems.VM.*;

/**
 *
 * @author brandon
 */
public abstract class Scheduler {
    public boolean running = false;
    
    public OS os;
    
    protected Semaphore waitingLock = new Semaphore(0, true);
    protected Semaphore readyLock = new Semaphore(0, true);
    
    private int memoryOffset = 0; //atomic because Java
    
    protected ArrayDeque<Program> waitingQueue = new ArrayDeque<Program>();
    protected ArrayDeque<Program> readyQueue = new ArrayDeque<Program>();
    protected ArrayDeque<Program> runningQueue = new ArrayDeque<Program>();
    protected ArrayDeque<Program> doneQueue = new ArrayDeque<Program>();
    
    public Scheduler() {
    }
    
    /**
     * Adds program to the waiting queue based on the scheduler policy
     * @param p
     */
    public abstract void addProgram(Program p) throws InterruptedException;
    
    /**
     * Loads as many processes as it can into memory from the waiting queue
     */
    protected synchronized void load() throws InterruptedException {
	if (!this.programsRunning()) {
	    if (!this.programsReady()) this.memoryOffset = 0;
	    
	    while (this.waitingLock.tryAcquire()) {
		Program program = this.waitingQueue.peekFirst();
		System.out.println("Attempting to load program " + program.pid + " into RAM @ " + this.memoryOffset);
		if (this.memoryOffset + program.size() > RAM.LENGTH) {
		    System.out.println("Program " + program.pid + " is too big to fit in the remainder of RAM");
		    this.waitingLock.release();
		    break;
		}
		else {
		    this.waitingQueue.pop();
		    program.memoryAddress = this.memoryOffset;
		    this.memoryOffset += program.size();
		    
		    System.out.println("Loading program " + program.pid + " into RAM @ " + this.memoryOffset);
		    this.os.vm.mmu.write(program, 0, this.os.vm.disk.read(program.diskAddress, program.size()));
		    
		    System.out.println("Adding program " + program.pid + " to the ready queue");
		    this.readyQueue.add(program);
		    program.state = ProgramState.READY;
		    Accounting.onProgramStateChange(program);
		    
		    this.readyLock.release();
		}
	    }
	}
    }
    
    
    /**
     * Returns whether or not there are programs ready for execution
     * @return 
     */
    public synchronized boolean programsWaiting() {
	return waitingQueue.size() > 0;
    }
    
    /**
     * Returns whether or not there are programs ready for execution
     * @return 
     */
    public synchronized boolean programsReady() {
	return readyQueue.size() > 0;
    }
    
    /**
     * Returns whether or not there are programs executing
     * @return 
     */
    public synchronized boolean programsRunning() {
	return runningQueue.size() > 0;
    }
    
    /**
     * Called by the OS when a CPU has finished processing a program
     * @param cpu 
     */
    public synchronized void haltHandler(CPU cpu) throws InterruptedException {
	this.runningQueue.remove(cpu.currentProgram);
	this.doneQueue.add(cpu.currentProgram);
	cpu.currentProgram.state = ProgramState.DONE;
	Accounting.onProgramStateChange(cpu.currentProgram);
	cpu.currentProgram = null;
	this.load();
    }
    
    /**
     * Waits for there to be a program available to run for this CPU
     * @param cpu
     * @throws java.lang.InterruptedException
     */
    public void schedule(CPU cpu) throws InterruptedException {
	this.readyLock.acquire();
	synchronized(readyQueue) {
	    Program currentProgram = this.readyQueue.pop();
	    cpu.currentProgram = currentProgram;
	
	    runningQueue.add(currentProgram);
	    currentProgram.state = ProgramState.RUNNING;
	    Accounting.onProgramStateChange(currentProgram);
	}
    }
}
