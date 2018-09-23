/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinycoin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The blockchain. It is composed by a linked list of set of block. Every element of the list
 * represent a level of the blockchain.
 * 
 * @author Giulio Auriemma
 */
public class BlockChain {
    public static final byte CONFIRM_BOUND = 3;
    public final List<Set<Block>> levels = new ArrayList();
    private final Map<Block, Block> orphanBlock = new HashMap();
    private boolean isForked = false;
    private final TCUser owner;
    
    public BlockChain(TCUser owner){
        this.owner = owner;
        this.levels.add(Collections.singleton(Block.STARTING_BLOCK));
    }
    
    /**
     * Adds a block into the chain, checking that following consesus rules hold:
     * 1. The father is in the chain at previous level.
     * 2. The list of transaction is not empty.
     * If the block is an orphan block, add it to the orphan set.
     * If the new block is father of an orphan block, add them to the chain, otherwise try
     * to resolve fork.
     * 
     * @param block     the block to add.
     * @return          true if block has been inserted, false otherwise.
     */
    public boolean addBlock(Block block){
        Block father = block.getFather();
        
        //If the father is not already in the chain, adds the block into the orphan block set.
        if(block.getLevel() > this.levels.size() || !this.levels.get(father.getLevel()).contains(father)){
            return this.orphanBlock.put(father, block) == null;
        }
        else if(block.getTransactions().size() > 0){
            //If the level of the block does not exist, create it.
            if(block.getLevel() == this.levels.size())
                this.levels.add(block.getLevel(), new LinkedHashSet());

            if( !this.levels.get(block.getLevel()).contains(block)){
                this.levels.get(block.getLevel()).add(block);
                if(this.levels.get(block.getLevel()).add(block))
                    this.isForked = true;

                /*If the block is a father of an orphaned block and no other block on its level has been
                confirmed, add its son to the chain. */ 
                if(!this.levels.get(block.getLevel()).stream().anyMatch(bl -> bl.isConfirmed()) 
                        && this.orphanBlock.get(block) != null){
                    this.addBlock(this.orphanBlock.get(block));
                    this.orphanBlock.remove(block);
                }
                else
                    this.tryResolveFork(block);
                
                return true;
            }
        }
        return false;
    }
    
    public boolean isForked(){
        return this.isForked;
    }
    
    /**
     * If the chain is forked, tries to resolve it. Two possible cases:
     * 1. the new block is in the main branch, then checks if the block on main branch could be confirmed.
     * 2. the new block is in a side branch, then checks if that branch could become main one.
     * 
     * @param newBlock the block added.
     */
    private void tryResolveFork(Block newBlock){
        if(newBlock.getFather().isOnMainBranch()){
            newBlock.setOnMainBranch(true);
            
            //Then try to confirm the main branch.
            int levelsOfMain = 0;
            //Check if the main branch has at least 6 block without branch, if so confirm all the block.
            while( this.levels.size() >= BlockChain.CONFIRM_BOUND && levelsOfMain < BlockChain.CONFIRM_BOUND && 
                    this.levels.get(newBlock.getLevel() - levelsOfMain).size() == 1 &&
                    !this.levels.get(newBlock.getLevel() - levelsOfMain).iterator().next().isConfirmed())
                levelsOfMain++;

            if(levelsOfMain == BlockChain.CONFIRM_BOUND) this.confirmMainBranch();
        }
        else if(this.isForked && !newBlock.getFather().isOnMainBranch()){
           //If the new block is on the outer level and it is the only one, it means that its side block is longer than the main one.
           if(newBlock.getLevel() == this.levels.size() - 1 && this.levels.get(newBlock.getLevel()).size() == 1)
               this.setNewMainBranch(newBlock);
        }
    }
    
    /**
     * Confirms all the block in the main branch and unconfirm all others.
     */
    private void confirmMainBranch(){
        Block currBlock = this.getLastBlock();
        while(currBlock.isOnMainBranch() && !currBlock.isConfirmed()){
            for(Block bl : this.levels.get(currBlock.getLevel())){
                if(!bl.equals(currBlock)) bl.unconfirm();
                else                      bl.confirm();
            }
            
            currBlock = currBlock.getFather();
        }   
        
        if(!currBlock.isOnMainBranch()) throw new RuntimeException("Malformed Blockchain.");
    }
    
    /**
     * Sets a side branch as the main one.
     * @param headBlock  the head of the new main branch.
     */
    private void setNewMainBranch(Block headBlock){
        Block currBlock = headBlock;
        while(!currBlock.isOnMainBranch()){
            currBlock.setOnMainBranch(isForked);
            currBlock = currBlock.getFather();
        }   
    }
    
    /**
     * Returns the first inserted block of the last level of the chain.
     * 
     * @return the first block of the last level.
     */
    public Block getLastBlock(){
        if(this.levels.get(this.levels.size() - 1).size() > 0)
            return this.levels.get(this.levels.size() - 1).iterator().next();
        throw new RuntimeException("Malformed Blockchain.");
    }
    
    /**
     * 
     * @return the length of the chain
     */
    public int getSize(){
        return this.levels.size();
    }
    
    /**
     * Returns a set of block in selected level.
     * 
     * @param level the selected level
     * @return      the set of block in selected level
     */
    public Set<? extends Block> getLevel(int level){
        return this.levels.get(level);
    }
}
