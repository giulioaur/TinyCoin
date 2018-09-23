/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import tinycoin.Block;

/**
 * Transport protocol of TinyCoin network. It computes the latency of the message basing on it's format.
 * 
 * @author Giulio Auriemma
 */
public class TCTrasport implements Transport{
    private final static String TX_DELAY = "txdelay", BLOCK_DELAY = "blockdelay";
    private final long txDelay, blockDelay;
    
    public TCTrasport(String suffix){
        this.txDelay = Configuration.getLong(suffix + "." + TX_DELAY);
        this.blockDelay = Configuration.getLong(suffix + "." + BLOCK_DELAY);
    }

    public Object clone(){
        return this;
    }
    
    @Override
    public void send(Node src, Node dest, Object o, int pid) {
        TCMessage msg = (TCMessage) o;
        long latency = msg.isSingleTx ? this.txDelay :
                this.blockDelay + this.txDelay * ((Block)msg.content).getTransactions().size();
        
        EDSimulator.add(latency, o, dest, pid);
    }

    @Override
    public long getLatency(Node node, Node node1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
