/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.logging.Level;
import java.util.logging.Logger;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import tinycoin.Block;
import tinycoin.MinerUser;
import tinycoin.TCUser;
import tinycoin.TCUser.MinerType;
import utils.Random;

/**
 * The protocol that represent the behaviour of miner.
 * 
 * @author Giulio Auriemma
 */
public class MiningProtocol implements EDProtocol, CDProtocol{
    private static final String TC_PROT_PID = "tc", MINE_BLOCK_CPU = "minecpu", MINE_BLOCK_GPU = "minegpu",
        MINE_BLOCK_FPGA = "minefpga", MINE_BLOCK_ASIC = "mineasic";
    private MiningStrategy strategy = null;
    private MinerUser miner = null;
    private final int tcProtPid, mineProbArr[] = new int[4];
    private int mineProb;
    
    public MiningProtocol(String suffix){
        this.tcProtPid = Configuration.getPid(suffix + "." + MiningProtocol.TC_PROT_PID);
        this.mineProbArr[MinerType.CPU.ordinal()] = Configuration.getInt(suffix + "." + MINE_BLOCK_CPU);
        this.mineProbArr[MinerType.GPU.ordinal()] = Configuration.getInt(suffix + "." + MINE_BLOCK_GPU);
        this.mineProbArr[MinerType.FPGA.ordinal()] = Configuration.getInt(suffix + "." + MINE_BLOCK_FPGA);
        this.mineProbArr[MinerType.ASIC.ordinal()] = Configuration.getInt(suffix + "." + MINE_BLOCK_ASIC);
    }
    
    
    /**
     * Istantiates the miner object and the mining strategy. The new miner become the main user of current node.
     * 
     * @param node the node to set as miner
     * @param pid  the pid of mining protocol
     */
    public void setAsMiner(Node node, int pid){
        Random.MinerCharacteristics minerChars = Random.RAND.getRandomMinerChar();
        
        this.miner = minerChars.getMiner();
        this.miner.addBitcoin(((TCProtocol)node.getProtocol(this.tcProtPid)).user.getCurrentBalance());
        this.strategy = minerChars.getStrategy();
        //Use the miner account as the default one.
        ((TCProtocol)node.getProtocol(this.tcProtPid)).user = this.miner;
        
        //Init the node following mining strategy
        this.strategy.initNode(node, tcProtPid, pid);
        
        //Get the probability to mine a block.
        this.mineProb = this.mineProbArr[this.miner.getType().ordinal()];
    }
    
    /**
     * Checks if current node is a miner one.
     * 
     * @return  true if it is a miner, false otherwise. 
     */
    public boolean isMiner(){
        return this.miner != null && this.strategy != null;
    }
    
    /**
     * Mine a block with the mining strategy.
     * 
     * @param node the current node
     * @param pid the mining protocol pid
     */
    public void mine(Node node, int pid){
        this.strategy.mine(this.miner, node, this.tcProtPid, pid);
    }
    
    /**
     * 
     * @return the miner user
     */
    public MinerUser getMiner(){
        return this.miner;
    }
    
    public Object clone(){
        MiningProtocol newMP = null;
        
        try {
            newMP = (MiningProtocol)super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MiningProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return newMP;
    }
    
    @Override
    public void processEvent(Node node, int i, Object o) {
        TCMessage msg = (TCMessage) o;
        
        if(!msg.isSingleTx){
            this.strategy.newBlock(this.miner, node, this.tcProtPid, i, (Block)msg.content);
        }
    }

    @Override
    public void nextCycle(Node node, int i) {
        if(this.miner != null && Random.RAND.PROVER.proofOfWork(this.mineProb))
            this.mine(node, i);
        
    }
    
    /**
     * 
     * @param miner     the new miner
     * @param strategy  the new mining strategy
     */
    public void setMinerAndStrategy(MinerUser miner, MiningStrategy strategy){
        if(miner != null)  this.miner = miner;
        if(strategy != null)  this.strategy = strategy;
    }
    
}
