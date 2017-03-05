/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.util.ArrayDeque;
import java.util.ArrayList;
import operatingsystems.VM.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class OS {
    private ProgramFileLoader pfl;
    
    private ArrayList<Program> programs = new ArrayList<Program>();
    
    private ArrayDeque<Program> readyQueue = new ArrayDeque<Program>();
    private ArrayDeque<Program> waitingQueue = new ArrayDeque<Program>();
    private ArrayDeque<Program> doneQueue = new ArrayDeque<Program>();
    
    private Scheduler scheduler;
    private VM vm;
    public OS(Scheduler scheduler, VM vm) {
	this.scheduler = scheduler;
	this.vm = vm;
	this.pfl = new ProgramFileLoader(vm);
    }
    
    /**
     * Loads, schedules, and runs all programs
     */
    void run() {
	// Load programs into RAM
	
	// Schedule programs
	
	// Create CPU execution threads
	
	// Foreach CPU loop:
	    // If the CPU is waiting, schedule a job
		// Run the scheduler to fill ready queue
		// Get the next program from the ready queue
		// If ready queue is empty, exit thread
		// Load program into cpu
	    // Else
		// Tell the CPU to execute until it is done
	
	throw new NotImplementedException();
    }
}
