/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

import operatingsystems.VM.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author brandon
 */
public class ProgramFileLoader {
    
    private VM vm;
    public ProgramFileLoader(VM vm) {
	this.vm = vm;
    }
    
    public void load(String filename) {
	throw new NotImplementedException();
    }
}
