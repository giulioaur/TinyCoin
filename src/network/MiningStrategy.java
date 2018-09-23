/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.transport.Transport;
import tinycoin.Block;
import tinycoin.MinerUser;

/**
 * An interface representing the mining strategy. This way, a mining strategy could be represented as lambda or
 * as method reference.
 * 
 * @author Giulio Auriemma
 */
public interface MiningStrategy {
    /**
     * Initialize the node in some special way. Called when the miners are initialized.
     * 
     * @param currNode       the current node
     * @param tcProtPid      the pid of TCProtocol
     * @param miningProtPid  the pid of MiningProtocol
     * @return               true if all go the right way, false otherwise
     */
    public boolean initNode(Node currNode, int tcProtPid, int miningProtPid);
    
    /**
     * Mines a new block basing on the mining strategy.
     * 
     * @param miner          the miner user
     * @param node           the miner node
     * @param tcProtPid      the pid of TCProtocol 
     * @param miningProtPid  the pid of MiningProtocol
     */
    public void mine(MinerUser miner, Node node, int tcProtPid, int miningProtPid);
    
    /**
     * The way the protocol reacts on new mined block. It's safe to call it only if canCallNewBlock() return true.
     * 
     * @param miner          the miner user
     * @param node           the miner node
     * @param tcProtPid      the pid of TCProtocol 
     * @param miningProtPid  the pid of MiningProtocol
     * @param newBlock       the mined block
     */
    public void newBlock(MinerUser miner, Node node, int tcProtPid, int miningProtPid, Block newBlock);
    
    /**
    * Propagates the block along the networks.
    * 
    * @param currNode   the current node
    * @param tcProtPid  the pid of TCProtocol
    * @param newBlock   the block to propagate
    */
    default void propagateBlock(Node currNode, int tcProtPid, Block newBlock){
        Linkable linkable = (Linkable) currNode.getProtocol(FastConfig.getLinkable(tcProtPid));
        TCMessage msg = new TCMessage(newBlock);

        for(int i = 0; i < linkable.degree(); i++){
            Node neig = linkable.getNeighbor(i);
            ((Transport)neig.getProtocol(FastConfig.getTransport(tcProtPid))).send(currNode, neig, msg, tcProtPid);
        }
    }
}
