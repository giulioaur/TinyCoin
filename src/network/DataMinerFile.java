/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.reports.GraphPrinter;
import tinycoin.Block;
import tinycoin.BlockChain;
import tinycoin.MinerUser;
import tinycoin.TCUser;
import tinycoin.TinyCoin;

/**
 * The data miner. All the data mined are written on a file .dat.
 * 
 * @author Giulio Auriemma
 */
public class DataMinerFile implements Control{
    private static final String TC_PROT_PID = "tc", SIMULATION_NAME = "name";
    private final int tcProtPid;
    private boolean isFirstTime = true;
    private DataOutputStream stream = null, stream2 = null;
    
    
//    public static TCUser sminer;
//    public static Set<TCUser> hminers = new HashSet();
//    
//    public static List<Block> honestBlocks = new LinkedList(), sybilBlocks = new LinkedList();
//    public static int app = 0;
    
    public DataMinerFile(String suffix){
        this.tcProtPid = Configuration.getPid(suffix + "." + TC_PROT_PID);
        
        try {
            File file = new File(System.getProperty("user.dir") + "\\log\\data\\" + TinyCoin.fileName + ".dat");
//                file2 = new File(System.getProperty("user.dir") + "\\log\\data\\" + TinyCoin.fileName + "bar.dat");
            file.createNewFile(); /*file2.createNewFile();*/
            
            this.stream = new DataOutputStream(new FileOutputStream(file));
//            this.stream2 = new DataOutputStream(new FileOutputStream(file2));
        } catch (IOException ex) {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public boolean execute() {
        if(!isFirstTime){
            
            final TCProtocol ex = (TCProtocol) Network.get(CommonState.r.nextInt(Network.size())).getProtocol(this.tcProtPid);
            final BlockChain ledger = ex.getUser().getLedger();

            for (int i = 0; i < ledger.getSize(); i++){
                try {                    
                    this.stream.writeInt(i);
                    this.stream.writeChar(' ');
                    this.stream.writeInt(ledger.getLevel(i).size());
                    this.stream.writeChar('\n');
                } catch (IOException ex1) {
                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
                
            try {
                this.stream.close();
            } catch (IOException ex1) {
                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex1);
            }
            
            
            
            
//            BlockChain ledgers[] ={ sminer.getLedger(), hminers.get(0).getLedger(), 
//                    ((TCProtocol) Network.get(CommonState.r.nextInt(Network.size())).getProtocol(this.tcProtPid)).getUser().getLedger(),
//                    ((TCProtocol) Network.get(CommonState.r.nextInt(Network.size())).getProtocol(this.tcProtPid)).getUser().getLedger()};
//            
//            ledgers[0].levels.get(1).clear();
//            
//            for (int i = 0; i < ledgers.length; i++){
//                Block blocks[] = ledgers[i].levels.stream().flatMap(Set::stream).toArray(Block[]::new);
//                
//                try {                    
//                    this.stream.writeInt(ledgers[i].levels.size());
//                    this.stream.writeChar(' ');
//                    this.stream.writeInt(blocks.length);
//                    this.stream.writeChar(' ');
//                    this.stream.writeInt(Arrays.stream(blocks).map(Block::getMiner).filter(hminers::contains).toArray().length);
//                    this.stream.writeChar('\n');
//                } catch (IOException ex1) {
//                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex1);
//                }
//            }

                    
                    
                    
//            final TCProtocol ex = (TCProtocol) Network.get(CommonState.r.nextInt(Network.size())).getProtocol(this.tcProtPid);
//            final BlockChain ledger = ex.getUser().getLedger();
//            int hblocks = 0, sblocks = 0;
//
//            for (int i = 1; i < ledger.getSize(); i++){
//                int lvhblocks = ledger.getLevel(i).stream().map(Block::getMiner).filter(hminers::contains).toArray().length;
//                hblocks += lvhblocks;
//                sblocks += ledger.getLevel(i).size() - lvhblocks;
//            }
//            
//            try {                    
//                this.stream2.writeInt(honestBlocks.size());
//                this.stream2.writeChar(' ');
//                this.stream2.writeInt(hblocks);
//                this.stream2.writeChar(' ');
//                this.stream2.writeInt(sybilBlocks.size());
//                this.stream2.writeChar(' ');
//                this.stream2.writeInt(sblocks);
//                this.stream2.writeChar('\n');
//            } catch (IOException ex1) {
//                Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex1);
//            }finally{
//                try {
//                    this.stream2.close();
//                } catch (IOException ex1) {
//                    Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex1);
//                }
//            }
        }
        this.isFirstTime = false;
        return false; 
    }
}
