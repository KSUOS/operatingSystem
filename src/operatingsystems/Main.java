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
import java.util.Map.Entry;
import javafx.util.Pair;
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
	
	int pageSize = 6;
	
	VM vm = new VM(1,pageSize);
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
	
	
	String processStats = "Program,Wait Time,Ready Time,Run Time,I/O Operations,Instructions Executed,Page Faults\n";
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
	    
	    processStats += p.ioOperations + "," + p.instructionsExecuted + "," + p.pageFaults + "\n";
	}
	
	Files.write(Paths.get("process_stats_" + s.getClass().getSimpleName()+ ".csv"), processStats.getBytes());
	
	if (pageSize == 0) {
	    System.out.println("Batch sizes:");
	    int pointer = 0;
	    for (Integer size : Accounting.batchSizes) {
		System.out.println("Batch #" + pointer + ": " + size * 4);
		pointer++;
	    }
	}
	else {
	    System.out.println("Memory usage:");
	    for (Pair<Long, Integer> pair : Accounting.pageUsage) {
		System.out.println(pair.getKey()+ "," + pair.getValue().floatValue()/ ((float)RAM.LENGTH / (float)os.vm.mmu.pageSize));
	    }
	}
    }
}
