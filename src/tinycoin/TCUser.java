/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinycoin;

import java.util.HashSet;
import java.util.Set;

/**
 * Expose the functions that all the users (miners and not) must provide.
 * 
 * @author Giulio Auriemma
 */
public abstract class TCUser {
    public enum MinerType{CPU, GPU, FPGA, ASIC};
    private final BlockChain ledger = new BlockChain(this);
    protected float balance;
    //Unconfirmed transaction pool.
    private final Set<Transaction> utx = new HashSet();
    
    /**
     * Creates a new transaction. If the amount is greater than the current balance of the user, the amount of the
     * new transaction is all the balance.
     * 
     * @param amount        the bitcoin to pass to the beneficiary.
     * @param beneficiary   the user that receive the bitcoin. 
     * @return              the new transaction if it was successfully created, null otherwise.
     */
    public Transaction createTransaction(float amount, TCUser beneficiary){
        if(balance <= 0)    return null;
        
        float currAmount = amount > balance ? balance : amount;
        
        Transaction newTransaction = new Transaction(this, currAmount, beneficiary);
        this.balance -= currAmount;
        
        return newTransaction;
    }
    
    /**
     * Adds the transaction to the list of non-confirmed transaction if the following consensus rules hold:
     * 1. It has not already been added.
     * If the transaction is null or already present, the utx list doesn't change.
     * 
     * @param newTransaction    the transaction to add.
     * @return                  true if transaction has been added, false if the input transaction is null or it is a duplicated.
     */
    public boolean recordNewTransaction(Transaction newTransaction){
        if(newTransaction == null) return false;
        
        return this.utx.add(newTransaction);
    }
    
    /**
     * Adds the new block to the ledger and deletes all its transaction from the list of unconfirmed ones.
     * 
     * @param newBlock  the block received.
     * @return          true if a block is correctly inserted in the blockchain, false otherwise.
     */
    public boolean recordNewBlock(Block newBlock){
        if(newBlock == null || !this.ledger.addBlock(newBlock)) return false;
        newBlock.getTransactions().forEach(tx -> this.utx.remove(tx));
        return true;
    }
    
    /**
     * Adds some bitcoin to the current user's balance.
     * @param toAdd the amount to add.
     */
    public void addBitcoin(float toAdd){
        this.balance += toAdd;
    }
    
    /**
     * 
     * @return the uneditable list of unconfirmed transaction
     */
    public Set<? extends Transaction> getUnconfirmedTx(){
        return this.utx;
    }
    
    /**
     * 
     * @return the current balance
     */
    public float getCurrentBalance(){
        return this.balance;
    }
    
    /**
     * 
     * @return the set of unconfirmed transaction.
     */
    protected Set<? extends Transaction> unconfirmedTransactions(){
        return this.utx;
    }
    
    /**
     * 
     * @return the ledger of the user.
     */
    public BlockChain getLedger(){
        return this.ledger;
    }
}
