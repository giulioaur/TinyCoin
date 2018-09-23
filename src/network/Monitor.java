/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import utils.Random;

/**
 *
 * @author Giulio Auriemma
 */
public class Monitor implements Control{
    private static final String CYCLES_NUM = "cyclesnum";
    private final int cyclesNum;
    private FileWriter wr;
    private int avg = 0, cycles = 0, currCycle = 0;
    private List<Integer> cyclesList = new LinkedList();
    
    public Monitor(String suffix){
        try {
            this.wr = new FileWriter(new File(System.getProperty("user.dir") + "\\log\\media\\media.txt"), true);
        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.cyclesNum = Configuration.getInt(suffix + "." + CYCLES_NUM);
        
    }
    
    @Override
    public boolean execute() {
        currCycle++;
        if(Random.RAND.PROVER.cycle == 1 && cycles > 1){
            this.cyclesList.add(cycles);
            avg = this.cyclesList.stream().reduce(0, Integer::sum) / this.cyclesList.size();
            cycles = 0;
            System.out.println("media: " + avg);
        }
        else{
            cycles++;
        }
        Random.RAND.PROVER.nextCycle();
        
        try {   
            if(this.currCycle == this.cyclesNum - 1){
                this.wr.write(avg + "\n");
                this.wr.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
}
