/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package operatingsystems.OS;

/**
 *
 * @author brandon
 */
public class PageTableEntry {
    public int page;
    public int physicalAddress;
    public boolean dirty = false;
}
