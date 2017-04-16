/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.util.ArrayList;

/**
 *
 * @author brandon
 */
public class Program {
    public int pid = 0;
    public int memoryAddress;
    public int diskAddress;
    public int priority;
    
    public ProgramState state = ProgramState.WAITING;
    
    public ArrayList<PageTableEntry> pageTable = new ArrayList<PageTableEntry>();
    
    // Stats
    public int instructionCount = 0;
    public int inputBufferSize = 0;
    public int outputBufferSize = 0;
    public int tempBufferSize = 0;
    
    public int ioOperations = 0;
    public int instructionsExecuted = 0;
    public int pageFaults = 0;
    
    public int faultAddress;
    public int[] cpuState = new int[17];
    
    public final int size() {
	return this.instructionCount + this.inputBufferSize + this.outputBufferSize + this.tempBufferSize;
    }
    
    @Override
    public String toString() {
	String pidString = "00" + Integer.toString(this.pid);
	pidString = pidString.substring(pidString.length() - 2);
	return "Program " + pidString;
    }
}
