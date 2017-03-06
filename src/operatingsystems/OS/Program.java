/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

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
    
    // Stats
    public int instructionCount = 0;
    public int inputBufferSize = 0;
    public int outputBufferSize = 0;
    public int tempBufferSize = 0;
    
    public final int size() {
	return this.instructionCount + this.inputBufferSize + this.outputBufferSize + this.tempBufferSize;
    }
    
    @Override
    public String toString() {
	return "Program " + this.pid + " (" + this.state.toString() + ")";
    }
}
