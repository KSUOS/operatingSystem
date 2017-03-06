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
import java.text.DecimalFormat;
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
	
	VM vm = new VM(4);
	OS os = new OS(s,vm);
	
	os.load("ProgramFile.txt");
	
	os.run();
	
	byte[] bytes = new byte[Disk.length * 4];
	int[] ints = vm.disk.read(0, Disk.length);
	for (int i = 0; i < Disk.length * 4; i+=4) {    
	    bytes[i + 0] = (byte)(ints[i/4] >> 0  & 0xFF);
	    bytes[i + 1] = (byte)(ints[i/4] >> 8  & 0xFF);
	    bytes[i + 2] = (byte)(ints[i/4] >> 16 & 0xFF);
	    bytes[i + 3] = (byte)(ints[i/4] >> 24 & 0xFF);
	}
	Files.write(Paths.get("disk.bin"), bytes);
	
	String cpuStats = "CPUID,CPU Time\n";
	for (CPU cpu : vm.cpus) {
	    long total = 0;
	    for (Long l : Accounting.cpuRunningTimes.get(cpu)) {
		total += l;
	    }
	    cpuStats += cpu + "," + ((float)total/1e6f) + "\n";
	}
	Files.write(Paths.get("cpu_stats.csv"), cpuStats.getBytes());
	
	
	String processStats = "Program,Wait Time,Ready Time,Run Time,I/O Operations,Instructions Executed\n";
	for (Program p : os.programs) {
	    processStats += p + ",";
	    Hashtable<ProgramState, Long> times = Accounting.programTimes.get(p);
	    
	    DecimalFormat format = new DecimalFormat("#.#######");
	    
	    String waitingTime = format.format((float)(times.get(ProgramState.READY) - times.get(ProgramState.WAITING))/(1e6f));
	    processStats += waitingTime + ",";
	    
	    String readyTime = format.format((float)(times.get(ProgramState.RUNNING) - times.get(ProgramState.READY))/(1e6f));
	    processStats += readyTime + ",";
	    
	    String runningTime = format.format((float)(times.get(ProgramState.DONE) - times.get(ProgramState.RUNNING))/(1e6f));
	    processStats += runningTime + ",";
	    
	    processStats += p.ioOperations + "," + p.instructionsExecuted + "\n";
	}
	
	Files.write(Paths.get("process_stats_" + s.getClass().getSimpleName()+ ".csv"), processStats.getBytes());
    }
}
