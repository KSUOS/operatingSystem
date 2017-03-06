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
public class SJFScheduler extends Scheduler {
    
    public synchronized void addProgram(Program p) throws InterruptedException {
	int pointer = 0;
	boolean found = false;
	for (Program searchingProgram : this.waitingQueue) {
	    if (searchingProgram.instructionCount > p.instructionCount) {
		this.waitingQueue.add(pointer, p);
		found = true;
		break;
	    }
	    pointer++;
	}
	if (!found) this.waitingQueue.add(p);
	
	p.state = ProgramState.WAITING;
	Accounting.onProgramStateChange(p);
	
	this.waitingLock.release(); // release a permit
    }
}