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
public abstract class Scheduler {
    public boolean running = false;
    
    private OS os;
    
    public Scheduler(OS os) {
	this.os = os;
    }
    
    public abstract void schedule();
}
