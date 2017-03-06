/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import operatingsystems.OS.*;
import operatingsystems.VM.*;

/**
 *
 * @author brandon
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
	Scheduler s = new FIFOScheduler();
	
	VM vm = new VM(1);
	OS os = new OS(s,vm);
	
	os.load("ProgramFile.txt");
	
	os.run();
	
	byte[] bytes = new byte[Disk.length * 4];
	int[] ints = vm.disk.read(0, Disk.length);
	for (int i = 0; i < Disk.length * 4; i+=4) {
	    bytes[i + 3] = (byte)(ints[i/4] >> 24 & 0xFF);
	    bytes[i + 2] = (byte)(ints[i/4] >> 16 & 0xFF);
	    bytes[i + 1] = (byte)(ints[i/4] >> 8  & 0xFF);
	    bytes[i + 0] = (byte)(ints[i/4] >> 0  & 0xFF);
	}
	Files.write(Paths.get("disk.bin"), bytes);
	
	System.out.println("\n");
	Thread.sleep(50);
	for (CPU cpu : vm.cpus) {
	    Duration total = Duration.ZERO;
	    for (Duration d : Accounting.cpuRunningTimes.get(cpu)) {
		total = total.plus(d);
	    }
	    System.out.println(cpu + " - Total CPU Time: " + ((float)total.getNano()/1e6f) + " ms");
	}
	
	System.out.println("");
	
	for (Program p : os.programs) {
	    Duration total = Duration.ZERO;
	    System.out.print(p + " - ");
	    Hashtable<ProgramState, Instant> times = Accounting.programTimes.get(p);
	    float waitingTime = (float)(Duration.between(times.get(ProgramState.WAITING),times.get(ProgramState.READY)).getNano())/(1e6f);
	    System.out.print("Wait: " + waitingTime + " ms");
	    System.out.print("\t");
	    
	    float readyTime = (float)(Duration.between(times.get(ProgramState.READY),times.get(ProgramState.RUNNING)).getNano())/(1e6f);
	    System.out.print("Ready: " + readyTime + " ms");
	    System.out.print("\t");
	    
	    float runningTime = (float)(Duration.between(times.get(ProgramState.RUNNING),times.get(ProgramState.DONE)).getNano())/(1e6f);
	    System.out.print("Run: " + runningTime + " ms");
	    System.out.print("\t");
	    
	    System.out.print("I/O Operations: " + (p.ioOperations));
	    
	    System.out.print("\t");
	    System.out.print("Instructions Executed: " + (p.instructionsExecuted));
	    
	    
	    System.out.println("");
	}	
    }    
}
