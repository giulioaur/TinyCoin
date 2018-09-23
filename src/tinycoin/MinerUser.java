/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinycoin;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A miner user.
 * 
 * @author Giulio Auriemma
 */
public class MinerUser extends TCUser{
    private final static int GREED_MULT = 10, MIN_TX = 500;
    private final MinerType type;
    private final byte greedPercentage;
    
    public MinerUser(TCUser.MinerType type, byte greedPercentage){
        this.type = type;
        this.greedPercentage = greedPercentage;
    }
    
    /**
     * 
     * @return the type of the miner
     */
    public MinerType getType(){
        return this.type;
    }

    /**
     * Mines a new block. The number of included transaction is computed with respect the greedPercentage of the miner.
     * 
     * @return the block mined.
     */
    public Block mineBlock() {
        final Set<Transaction> txToInclude = new HashSet();
        
        txToInclude.addAll(this.unconfirmedTransactions().stream()
                                        .filter(tx -> this.shouldMineMore(txToInclude.size()))
                                        .collect(Collectors.toList()));
        
        if(txToInclude.isEmpty()){
            if(this.unconfirmedTransactions().isEmpty())    return null;
            txToInclude.add(this.unconfirmedTransactions().toArray(new Transaction[1])[0]);
        }
        
        //The father of the new block is the first block of the last level.
        return new Block(txToInclude, this.getLedger().getLastBlock(), this);
    }
    
    /**
     * Determines if it's worth to mine more. It exploit its greed and a minimum worth balance value.
     * 
     * @param txReward the reward for every transaction
     * @param minedTx  the number of already mined transactions
     * @return         true if it is worth to mine on, false if the miner have to stop
     */
    private boolean shouldMineMore(int minedTx){
        if(minedTx <= MIN_TX && minedTx * Block.TX_REWARD + this.balance < this.greedPercentage * GREED_MULT)
            return true;
        
        return (Math.random() * 100) < greedPercentage + Block.TX_REWARD;
    }
}
