/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.io.*;
import java.io.File;
import operatingsystems.VM.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class ProgramFileLoader {

    private OS os;
    private File file;

    public ProgramFileLoader(OS os) {
	this.os = os;
    }

    /**
     * Opens the file, creates a Program, loading the opcodes into the vm disk beginning at offset 0 and add the Program to this.os.programs
     *
     * @param filename
     */
    public void load(String filename) throws InterruptedException {
	this.file = new File(filename);
	try {
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    String line;
	    int instructionno = 0; //current instruction number (excludes lines that begin with '//')
	    Program currentProgram = null;
	    try {
		while ((line = br.readLine()) != null) {

		    /*
			Create and initialize the Program object's pid, instructionCount, and diskAddress.
		     */
		    if (line.contains("JOB")) {
			String[] s = line.split(" ");
			currentProgram = new Program();
			currentProgram.pid = Integer.parseInt(s[2], 16);
			currentProgram.instructionCount = Integer.parseInt(s[3], 16);
			currentProgram.priority = Integer.parseInt(s[4], 16);
			currentProgram.diskAddress = instructionno;
			this.os.programs.add(currentProgram);
		    } 
		    /*
			Set the Program object's buffer size variables and write buffers into
			disk.
		     */ 
		    else if (line.contains("Data")) {
			String[] s = line.split(" ");
			currentProgram.inputBufferSize = Integer.parseInt(s[2], 16);
			currentProgram.outputBufferSize = Integer.parseInt(s[3], 16);
			currentProgram.tempBufferSize = Integer.parseInt(s[4], 16);
		    } else if (line.contains("END")) {
//			break;
		    }
		    /*
			Write a programs instructions into disk.
		     */ 
		    else {
			line = line.replace(" ", "");
			line = line.substring(2);
			long b = Long.parseLong(line, 16);
			int a = (int) b;
			this.os.vm.disk.write(instructionno, a);
			instructionno++;
		    }
		}
	    } catch (IOException ex) {
		System.out.println("IOException " + ex.toString());
	    }
	} catch (FileNotFoundException ex) {
	    System.out.println("File not found");
	}
    }
}
