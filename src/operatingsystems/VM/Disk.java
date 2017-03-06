/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.VM;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
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
    private RandomAccessFile file;

    public Disk(VM vm) throws FileNotFoundException {
	this.vm = vm;

	this.file = new RandomAccessFile("DataFile", "rw");
    }

    /**
     * Read a word from the disk
     *
     * @param offset
     * @return
     */
    public int read(int offset) throws IOException {
	synchronized(this.file) {
	    this.file.seek(offset * 4);
	    return this.file.readInt();
	}
    }

    /**
     * Read multiple words from the disk
     *
     * @param offset
     * @param length
     * @return
     */
    public int[] read(int offset, int length) throws IOException {
	byte[] bytes = new byte[length * 4];
	synchronized(this.file) {
	    this.file.seek(offset * 4);
	    this.file.read(bytes);
	}
	
	IntBuffer ib = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
	int[] array  = new int[ib.remaining()];
	ib.get(array);
	return array;
    }

    /**
     * Write a word to the disk
     *
     * @param offset
     * @param word
     */
    public void write(int offset, int word) throws IOException {
	synchronized(this.file) {
	    this.file.seek(offset * 4);
	    byte[] w = new byte[4];
	    w[0] = (byte)(word >> 0 & 0xFF);
	    w[1] = (byte)(word >> 8 & 0xFF);
	    w[2] = (byte)(word >> 16 & 0xFF);
	    w[3] = (byte)(word >> 24 & 0xFF);
	    this.file.write(w);
	}
    }

    /**
     * Write multiple words to the disk
     *
     * @param offset
     * @param words
     */
    public void write(int offset, int[] words) throws IOException {
	ByteBuffer bb = ByteBuffer.allocate(words.length * 4);
	for (int i = 0; i < words.length; i++) {
	    bb.putInt(i * 4, words[i]).array();
	}
	synchronized(this.file) {
	    this.file.seek(offset * 4);
	    this.file.write(bb.array());
	}
    }
    
    
    public void print() throws IOException {
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
