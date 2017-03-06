/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import operatingsystems.OS.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class VM {
    public MMU mmu;
    public Disk disk;
    public ArrayList<CPU> cpus = new ArrayList<CPU>();
    public boolean shuttingDown = false;
    
    public VM(int numberCPUs) throws FileNotFoundException {
	this.disk = new Disk(this);
	this.mmu = new MMU(this);
	for (byte i = 0; i < numberCPUs; i++) {
	    this.cpus.add(new CPU(this, i));
	}
    }
    
    public void shutdown() throws InterruptedException {
	this.shuttingDown = true;
	Thread self = Thread.currentThread();
	for (CPU cpu : this.cpus) {
	    if (cpu != self) {
		cpu.interrupt();
	    }
	}
    }
}
