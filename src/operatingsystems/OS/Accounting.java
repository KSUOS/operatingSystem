/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.time.*;
import java.util.*;
import operatingsystems.VM.*;

/**
 *
 * @author brandon
 */
public class Accounting {
    
    public static Hashtable<CPU, Instant> lastCPUHaltTime = new Hashtable<CPU, Instant>();
    public static Hashtable<CPU, ArrayList<Duration>> cpuRunningTimes = new Hashtable<CPU, ArrayList<Duration>>();
    
    public static void onCPUStateChange(CPU cpu) {
	if (cpu.state == CPUState.HALTED) {
	    lastCPUHaltTime.put(cpu, Instant.now());
	}
	else {
	    Duration d = Duration.between(lastCPUHaltTime.get(cpu), Instant.now());
	    cpuRunningTimes.putIfAbsent(cpu, new ArrayList<Duration>());
	    cpuRunningTimes.get(cpu).add(d);
	}
    }
    
    public static Hashtable<Program, Hashtable<ProgramState, Instant>> programTimes = new Hashtable<Program, Hashtable<ProgramState, Instant>>();
    public static void onProgramStateChange(Program p) {
	programTimes.putIfAbsent(p, new Hashtable<ProgramState, Instant>());
	programTimes.get(p).put(p.state, Instant.now());
    }
}
