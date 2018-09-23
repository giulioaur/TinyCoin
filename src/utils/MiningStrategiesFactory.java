/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.LinkedList;
import java.util.List;
import network.DataMinerFile;
import network.MiningProtocol;
import tinycoin.Block;
import tinycoin.MinerUser;
import network.MiningStrategy;
import network.TCProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import tinycoin.BlockChain;

/**
 * A factory that return a mining strategy. If new fraudolent mining strategies want to be added, do it in this class.
 *
 * @author Giulio Auriemma
 */
public class MiningStrategiesFactory {
    /**
     * The list of mining strategy.
     */
    public static final String HONEST = "honest", SELFISH = "selfish", SYBIL = "sybil";
    
    /**
     * Return the selected mining strategy.
     * 
     * @param strategyName  the name of mining strategy
     * @return              the selected mining strategy, null if no such strategy exists.
     */
    public static MiningStrategy getMiningStrategy(String strategyName){
        switch(strategyName){
            case HONEST:
                return new HonestMining();
            case SELFISH:
                return new SelfishMining();
            case SYBIL:
                return new SybilMining();
            default:
                return null;
        }
    }
    
    /**
     * Honest mining strategy.
     */
    private static final class HonestMining implements MiningStrategy{
        @Override
        public void mine(MinerUser miner, Node node, int tcProtPid, int miningProtPid) {
            //Mines a block and announce it.
            Block minedBlock = miner.mineBlock();
            if(minedBlock != null && minedBlock.getTransactions().size() > 0){
//                DataMinerFile.honestBlocks.add(minedBlock);
                
                System.out.println("Block mined with " + minedBlock.getTransactions().size() + " txs. By miner " + miner.getType());
                ((TCProtocol)node.getProtocol(tcProtPid)).addBlock(minedBlock, node, tcProtPid);
            }
        }

        @Override
        public void newBlock(MinerUser miner, Node node, int tcProtPid, int miningProtPid, Block newBlock) {
            //Simply propagates the block.
            this.propagateBlock(node, tcProtPid, newBlock);
        }

        @Override
        public boolean initNode(Node currNode, int tcProtPid, int miningProtPid) {
            return true;
        }

    }
    
    /**
     * Selfish mining strategy.
     */
    private static final class SelfishMining implements MiningStrategy{
        List<Block> privateChain = new LinkedList();
        Block lastBlock = null;  
        
        /**
         * Mines a new block and take it for its own private chain. If after the mining the chain has 
         * reached the confirmation bound, announce all its blocks, since is sure that it will be 
         * confirmed. To make the mined block the one to be chosen by the miner in next mining, 
         * add it to its ledger.
         */
        @Override
        public void mine(MinerUser miner, Node node, int tcProtPid, int miningProtPid) {
            Block minedBlock = miner.mineBlock();
            
            if(((TCProtocol)node.getProtocol(tcProtPid)).getUser().recordNewBlock(minedBlock)){
//                DataMinerFile.honestBlocks.add(minedBlock);
                this.privateChain.add(minedBlock);
                this.lastBlock = minedBlock;
                
                if(this.privateChain.size() > BlockChain.CONFIRM_BOUND){
                    this.privateChain.forEach(bl -> this.propagateBlock(node, tcProtPid, bl));
                    this.privateChain.clear();
                }
            }
        }

        /**
         * If this selfish miner has recently mined and the new block is not its own one, follow one strategy 
         * (where n is the lenght of its private chain):
         * 1. If no not already announced block is on private chain and new block has level n, than stops 
         *    mining on private chain. The honest miners win.
         * 2. The current block has level n, so it announces all the blocks in its chain and continues 
         *    mining on that chain. Now same length.
         * 3. The current block has level n-1, so publishes all the chain and tries to mining again on its chain.
         * 4. The current block has level n-j, with j >= 2, so it announces all the j - 1 blocks and continues
         *    to mine on its private chain. If its private chain length is less than j, announces all blocks.
         */
        @Override
        public void newBlock(MinerUser miner, Node node, int tcProtPid, int miningProtPid, Block newBlock) {
            if(this.lastBlock != null && !newBlock.getMiner().equals(miner)){
                if(newBlock.getLevel() >= this.lastBlock.getLevel()){
                    if(this.privateChain.isEmpty())
                        this.lastBlock = null;
                    else{
                        this.privateChain.forEach(bl -> this.propagateBlock(node, tcProtPid, bl));
                        this.privateChain.clear();
                    }
                }
                else if(newBlock.getLevel() == this.lastBlock.getLevel() - 1){
                    this.privateChain.forEach(bl -> this.propagateBlock(node, tcProtPid, bl));
                    this.privateChain.clear();
                }
                else{
                    int j = this.lastBlock.getLevel() - newBlock.getLevel();
                    List<Block> toAnnounce = this.privateChain.size() >= j-1 ? 
                            this.privateChain.subList(0, j-1) : 
                            this.privateChain;
                    
                    toAnnounce.forEach(bl -> this.propagateBlock(node, tcProtPid, bl));
                    if(this.privateChain.size() == toAnnounce.size()) this.privateChain.clear();
                    else                                this.privateChain.removeAll(toAnnounce);
                }
            }
            
            /* Finally propagates also the new block. This behaviour could be avoid to help the selfish
               mining strategy. */
            this.propagateBlock(node, tcProtPid, newBlock);
        }

        @Override
        public boolean initNode(Node currNode, int tcProtPid, int miningProtPid) {
            return true;
        }

    };
    
    /**
     * Selfish mining strategy.
     */
    private static final class SybilMining implements MiningStrategy{
        
        /**
         * Mines a new block and propagates it.
         */
        @Override
        public void mine(MinerUser miner, Node node, int tcProtPid, int miningProtPid) {
            Block minedBlock = miner.mineBlock();
            if(minedBlock != null && minedBlock.getTransactions().size() > 0){
//                DataMinerFile.sybilBlocks.add(minedBlock);
                System.out.println("Block mined with " + minedBlock.getTransactions().size() + " txs. By sybil miner " + miner.getType());
                ((TCProtocol)node.getProtocol(tcProtPid)).addBlock(minedBlock, node, tcProtPid);
            }
        }

        /**
         * If the new block has not been mined by the sybil miner, doesn't propagate it.
         */
        @Override
        public void newBlock(MinerUser miner, Node node, int tcProtPid, int miningProtPid, Block newBlock) {
            if(newBlock.getMiner().equals(miner))
                this.propagateBlock(node, tcProtPid, newBlock);
        }
        
        /**
         * To simulate the sybil attack, some of the Network's node become account of sybil miner. The chosen
         * nodes are normal node (not miner).
         */
        @Override
        public boolean initNode(Node currNode, int tcProtPid, int miningProtPid) {
            MinerUser miner = (MinerUser)((TCProtocol)currNode.getProtocol(tcProtPid)).getUser();
            MiningStrategy mining = this;
            
            final int minAccounts = Configuration.getInt("MiningStrategy.sybil.minaccounts"),
                      maxAccounts = Configuration.getInt("MiningStrategy.sybil.maxaccounts"),
                      numOfAccounts = maxAccounts - minAccounts > 0 ? 
                                      CommonState.r.nextInt(maxAccounts - minAccounts) + minAccounts:
                                      0,
                      minerNum = Configuration.getInt("MiningStrategy.sybil.minernum");
            
            for(int i = 0; i < numOfAccounts; i++){
                Node node = Network.get(CommonState.r.nextInt(Network.size() - minerNum) + minerNum);
                
                //Be sure that the chosen node are not own by another sybil.
                while(((MiningProtocol)node.getProtocol(miningProtPid)).isMiner())
                    node = Network.get(CommonState.r.nextInt(Network.size() - minerNum) + minerNum);
                
                ((MiningProtocol)node.getProtocol(miningProtPid)).setMinerAndStrategy(miner, mining);
            }
            
            return true;
        }

    };
}
