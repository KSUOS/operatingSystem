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
	    bytes[i + 3] = (byte)(ints[i/4] >> 24 & 0xFF);
	    bytes[i + 2] = (byte)(ints[i/4] >> 16 & 0xFF);
	    bytes[i + 1] = (byte)(ints[i/4] >> 8  & 0xFF);
	    bytes[i + 0] = (byte)(ints[i/4] >> 0  & 0xFF);
	}
	Files.write(Paths.get("disk.bin"), bytes);
    }
    
}
