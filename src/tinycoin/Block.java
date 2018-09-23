/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinycoin;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A block of transaction. When a block is created a reference to its miner, its father and its fee must
 * be provided. A block could be on a main branch or a side branch, and once it was confirmed / unconfirmed
 * it could not be again.
 * 
 * @author Giulio Auriemma
 */
public class Block {
    /**
     * The first block of all the blockchains.
     */
    public static final Block STARTING_BLOCK = new Block();
    
    public static float BLOCK_REWARD, TX_REWARD ;
    private static int BLOCK_NUM = 1;
    private final int id, level;
    private final MinerUser miner;
    private final float fee;
    private final Block father;
    private final Set<Transaction> transactions;
    private boolean isOnMainBranch = false, isConfirmed = false, couldConfirm = true;
    
    private Block(){
        this.id = 0;
        this.level = 0;
        this.father = null;
        this.fee = 0;
        this.miner = null;
        this.transactions = null;
        this.isOnMainBranch = true;
        this.isConfirmed = true;
    }
    
    public Block(Set<Transaction> transactions, Block father, MinerUser miner){
        this.id = Block.BLOCK_NUM++;
        this.transactions = transactions;
        this.father = father;
        this.miner = miner;
        this.fee = this.transactions.size() * TX_REWARD + BLOCK_REWARD;
        this.level = this.father.level + 1;
    }
    
    /**
     * 
     * @return the set of the transaction.
     */
    public Set<? extends Transaction> getTransactions(){
        return this.transactions;
    }
    
    /**
     * 
     * @return the level of the block.
     */
    public int getLevel(){
        return this.level;
    }
    
    /**
     * 
     * @return the father block of this block.
     */
    public Block getFather(){
        return this.father;
    }
    
    /**
     * 
     * @return the user who mined this block.
     */
    public MinerUser getMiner(){
        return this.miner;
    }
    
    /**
     * 
     * @return the ffe for the miner of the block.
     */
    public float getFee(){
        return this.fee;
    }
    
    /**
     * 
     * @return true if the block is on main branch, false otherwise.
     */
    public boolean isOnMainBranch(){
        return this.isOnMainBranch;
    }
    
    /**
     * 
     * @param value pass true to set current block on main branch, false to set it on a side branch.
     */
    public void setOnMainBranch(boolean value){
        this.isOnMainBranch = value;
    }
    
    /**
     * 
     * @return true if the block has been confirmed yet.
     */
    public boolean isConfirmed(){
        return this.isConfirmed;
    }
    
    /**
     * Confirms all the transactions on the current block, paying all the transaction's beneficiary. 
     * After that operation the block could not be confirmed / unconfirmed again.
     * 
     */
    public void confirm(){
        if(this.couldConfirm){
            //Confirm the transaction and make its confirmed state not changeble. Then confirm all transactions.
            final List<Transaction> toDelete = new LinkedList();
            this.couldConfirm = false;
            this.isConfirmed = true;
            this.transactions.forEach(tr -> {
                tr.getOutput().addBitcoin(tr.getAmount());
                if(toDelete.size() < this.transactions.size() - 1) toDelete.add(tr);
            });

            this.transactions.removeAll(toDelete);
        }
    }
    
    /**
     * Unconfirms all the transactions on the current block, returning all the bitcoin spent to the transaction's maker. 
     * After that operation the block could not be confirmed / unconfirmed again.
     * 
     */
    public void unconfirm(){
        if(this.couldConfirm){  
            //Confirm the transaction and make its confirmed state not changeble. Then return all transaction's amount to its maker.
            final List<Transaction> toDelete = new LinkedList();
            this.couldConfirm = false;
            this.isConfirmed = false;
            this.transactions.forEach(tr -> {
                tr.getInput().addBitcoin(tr.getAmount());
                if(toDelete.size() < this.transactions.size() - 1) toDelete.add(tr);
            });

            this.transactions.removeAll(toDelete);
        }
    }
    
    @Override
    public int hashCode() { 
        return Integer.valueOf(this.id).hashCode(); 
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Block && Integer.valueOf(this.id).equals(((Block) obj).id);
    }
}
