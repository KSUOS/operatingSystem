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
public class FIFOScheduler extends Scheduler {
    
    public synchronized void addProgram(Program p) throws InterruptedException {
	this.waitingQueue.add(p); // add to end of waiting queue
	p.state = ProgramState.WAITING;
	Accounting.onProgramStateChange(p);
	this.waitingLock.release(); // release a permit
    }
}
