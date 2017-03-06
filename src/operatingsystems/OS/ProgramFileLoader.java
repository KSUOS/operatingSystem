/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import java.io.File;
import operatingsystems.VM.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class ProgramFileLoader {
    
    private VM vm;
    private File file;
    
    public ProgramFileLoader(VM vm) {
	this.vm = vm;
    }
    
    /**
     * Opens the file, creates a Program, loading the opcodes into the vm disk beginning at offset 0 and add the Program to this.os.programs
     * @param filename 
     */
    public void load(String filename) {
	this.file = new File(filename);
	try{
		BufferedReader br = new BufferedReader (new FileReader(filename));
			String line;
			int programno = 0; //current pcb
			int lineno = 0; //current line of source file
			int instructionno = 0; //current instruction number (excludes lines that begin with '//')
			
			try{
				while((line = br.readLine()) != null){	
					
					if(line.contains("JOB")){
						String[] s = line.split(" ");
						Program p = new Program();
						programs.add(p);
						p.pid = Integer.parseInt(s[2], 16);
						p.instructionCount = Integer.parseInt(s[3], 16);
						p.diskAddress = instructionno;
						programno++;
					}
					else if(line.contains("Data")){
						String[] s = line.split(" ");
						programs.get(programno-1).inputBufferSize = Integer.parseInt(s[2], 16);
						programs.get(programno-1).outputBufferSize = Integer.parseInt(s[3], 16);
						programs.get(programno-1).tempBufferSize = Integer.parseInt(s[4], 16);
					}
					else if(line.contains("END")){
						
					}
					else{
						line = line.replace(" ", "");
						line = line.substring(2);
						long b = Long.parseLong(line, 16);
						int a = (int)b;
						d.write(instructionno, a);
						instructionno++;
					}
					lineno++;
				}
				//System.out.println(lineno);
			}catch(IOException ex){
				System.out.println("IOException");
			}
		}catch(FileNotFoundException ex){
			System.out.println("File not found");
		}
    }
	
	throw new NotImplementedException();
    }
}
