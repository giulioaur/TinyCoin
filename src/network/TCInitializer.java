/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import tinycoin.Block;

/**
 * The network initializer.
 * 
 * @author Giulio Auriemma
 */
public class TCInitializer implements Control{
    private static final String MINER_PERCENTAGE = "minerpercentage", TC_PROT_PID = "tc", MINING_PROT_PID = "mining";
    private final int minerNumber, tcProtPid, miningProtPid;
    
    public TCInitializer(String suffix){
        this.minerNumber = (int)(Network.size() * ((float)Configuration.getInt(suffix + "." + MINER_PERCENTAGE) / 100));
        this.tcProtPid = Configuration.getPid(suffix + "." + TC_PROT_PID);
        this.miningProtPid = Configuration.getPid(suffix + "." + MINING_PROT_PID);
    }
    
    @Override
    public boolean execute() {
        //Init costant
        Block.BLOCK_REWARD = Configuration.getInt("block.reward");
        Block.TX_REWARD = Configuration.getInt("block.tx.reward");
        
        //Init miner
        for(int i = 0; i < this.minerNumber; i++){
            MiningProtocol miner = (MiningProtocol)Network.get(i).getProtocol(this.miningProtPid);
            miner.setAsMiner(Network.get(i), this.miningProtPid);
        }
        return false;
    }
}
