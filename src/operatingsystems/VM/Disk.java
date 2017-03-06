/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import java.io.File;
import java.util.Arrays;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Reads raw words from the binary disk file "DataFile"
 * @author brandon
 */
public class Disk {
    public static final int length = 2048;

    private VM vm;
    private int[] buffer = new int[Disk.length];
    private File file;
    
    public int delay = 0;

    public Disk(VM vm) {
	this.vm = vm;

	this.file = new File("DataFile");
    }

    /**
     * Read a word from the disk
     *
     * @param offset
     * @return
     */
    public int read(int offset) throws InterruptedException {
	Thread.sleep(0,this.delay); //TODO remove
	return this.buffer[offset];
    }

    /**
     * Read multiple words from the disk
     *
     * @param offset
     * @param length
     * @return
     */
    public int[] read(int offset, int length) throws InterruptedException {
	Thread.sleep(0,this.delay); //TODO remove
	return Arrays.copyOfRange(this.buffer, offset, offset + length);
    }

    /**
     * Write a word to the disk
     *
     * @param offset
     * @param word
     */
    public void write(int offset, int word) throws InterruptedException {
	Thread.sleep(0,this.delay); //TODO remove
	this.buffer[offset] = word;
    }

    /**
     * Write multiple words to the disk
     *
     * @param offset
     * @param words
     */
    public void write(int offset, int[] words) throws InterruptedException {
	Thread.sleep(0,this.delay); //TODO remove
	for (int i = offset; i < offset + words.length; i++) {
	    this.buffer[i] = words[i - offset];
	}
    }
    
    
    public void print() throws InterruptedException {
	int lineSize = 1;
	for (int i = 0; i < Disk.length / lineSize; i++) {
	    int[] toWrite = this.read(i, lineSize);
	    String prefix = "";
	    for (int j = 0; j < lineSize; j++) {
		String word = "00000000" + Integer.toHexString(toWrite[j]);
		word = "0x" + word.substring(word.length() - 8);
		System.out.print(prefix + word);
		prefix = " ";
	    }
	    System.out.print("\n");
	}
    }
}
