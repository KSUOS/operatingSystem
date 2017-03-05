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
	
	throw new NotImplementedException();
    }
}
