/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.time.*;
import java.util.*;
import java.util.Map.*;
import javafx.util.Pair;
import operatingsystems.VM.*;

/**
 *
 * @author brandon
 */
public class Accounting {
    
    public static Hashtable<CPU, Long> lastCPUHaltTime = new Hashtable<CPU, Long>();
    public static Hashtable<CPU, ArrayList<Long>> cpuRunningTimes = new Hashtable<CPU, ArrayList<Long>>();
    
    public static void onCPUStateChange(CPU cpu) {
	if (cpu.state == CPUState.HALTED) {
	    lastCPUHaltTime.put(cpu, System.nanoTime());
	}
	else {
	    Long l = System.nanoTime() - lastCPUHaltTime.get(cpu);
	    cpuRunningTimes.putIfAbsent(cpu, new ArrayList<Long>());
	    cpuRunningTimes.get(cpu).add(l);
	}
    }
    
    public static Hashtable<Program, Hashtable<ProgramState, Long>> programTimes = new Hashtable<Program, Hashtable<ProgramState, Long>>();
    public static void onProgramStateChange(Program p) {
	programTimes.putIfAbsent(p, new Hashtable<ProgramState, Long>());
	programTimes.get(p).putIfAbsent(p.state, System.nanoTime());
    }
    
    public static ArrayList<Integer> batchSizes = new ArrayList<Integer>();
    public static void addBatchSize(int size) {
	batchSizes.add(size);
    }
    
    public static ArrayList<Pair<Long, Integer>> pageUsage = new ArrayList<Pair<Long, Integer>>();
    public static void onPageUsageChange(int usage) {
	pageUsage.add(new Pair(System.nanoTime(), usage));
    }
}
