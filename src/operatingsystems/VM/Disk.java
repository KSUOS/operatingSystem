/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import java.io.File;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 
 * @author brandon
 */
public class Disk {
    
    private VM vm;
    private int[] buffer = new int[2048];
    private File file;
    
    public Disk(VM vm) {
	this.vm = vm;
	
	this.file = new File("DataFile");
    }
    
    /**
     * Read a word from the disk
     * @param offset
     * @return 
     */
    public int read(int offset) {
	throw new NotImplementedException();
    }
    
    /**
     * Read multiple words from the disk
     * @param offset
     * @param length
     * @return 
     */
    public int[] read(int offset, int length) {
	throw new NotImplementedException();
    }
    
    /**
     * Write a word to the disk
     * @param offset
     * @param word 
     */
    public void write(int offset, int word) {
	throw new NotImplementedException();
    }
    
    /**
     * Write multiple words to the disk
     * @param offset
     * @param words 
     */
    public void write(int offset, int[] words) {
	throw new NotImplementedException();
    }
}
