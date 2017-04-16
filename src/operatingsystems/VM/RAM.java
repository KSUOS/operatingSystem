/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Allows access into RAM (Random access memory)
 * @author brandon
 */
public class RAM {
    public static final int LENGTH = 1024;
    private int[] memory = new int[RAM.LENGTH];
    
    private VM vm;
    
    public RAM(VM vm) {
	this.vm = vm;
    }
    
    /**
     * Reads a word from memory
     * @param offset
     * @return 
     */
    public int read(int offset) {
	if (offset > 1024 || offset < 0) throw new Error("Memory read offset out of bounds");
	return this.memory[offset];
    }
    
    /**
     * Write a word to memory
     * @param offset
     * @param word 
     */
    public void write(int offset, int word) {
	if (offset > 1024 || offset < 0) throw new Error("Memory write offset out of bounds");
	this.memory[offset] = word;
    }
}
