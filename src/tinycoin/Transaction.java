/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinycoin;

/**
 * A transaction between two user.
 * 
 * @author Giulio Auriemma
 */
public class Transaction {
    private static int TRANSACTION_NUM = 0;
    private final TCUser input, output;
    private final float amount;
    private final int id;
    
    public Transaction(TCUser input, float amount, TCUser output){
        this.input = input;
        this.amount = amount;
        this.output = output;
        this.id = Transaction.TRANSACTION_NUM++;
    }
    
    /**
     * 
     * @return the user that make the transaction.
     */
    public TCUser getInput(){
        return this.input;
    }
    
    /**
     * 
     * @return the beneficiary of the transaction.
     */
    public TCUser getOutput(){
        return this.output;
    }
    
    /**
     * 
     * @return the amount of bitcoin passed in the transaction.
     */
    public float getAmount(){
        return this.amount;
    }
    
    @Override
    public int hashCode() { 
        return Integer.valueOf(this.id).hashCode(); 
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Transaction && Integer.valueOf(this.id).equals(((Transaction) obj).id);
    }
}
