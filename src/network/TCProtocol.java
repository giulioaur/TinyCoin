/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;
import tinycoin.Block;
import tinycoin.NormalUser;
import tinycoin.TCUser;
import tinycoin.Transaction;

/**
 * The base protocol of TinyCoin network.
 *
 * @author Giulio Auriemma
 */
public class TCProtocol implements EDProtocol, CDProtocol, Linkable{
    private static final String MAX_BALANCE = "account.maxbalance", MIN_BALANCE = "account.minbalance", 
        TX_PROB ="txprob", MINING_PROT_PID = "mining";
    private final int tx_prob, miningProtPid, txMaxAmount;
    private List<Node> neighborhood = new ArrayList();
    TCUser user = new NormalUser();
    private MiningStrategy strategy = null; 
    
    public TCProtocol(String suffix){
        this.tx_prob = Configuration.getInt(suffix + "." + TX_PROB);
        this.miningProtPid = Configuration.getPid(suffix + "." + MINING_PROT_PID);
        
        //Set the starting user's balance.
        final int max = Configuration.getInt(MAX_BALANCE), min = Configuration.getInt(MIN_BALANCE);
        this.user.addBitcoin((float) CommonState.r.nextInt(max - min) + min);
        this.txMaxAmount = Configuration.getInt("tx.maxamount");
    }

    @Override
    public Object clone(){
        TCProtocol newProt = null;
        
        try {
            final int max = Configuration.getInt(MAX_BALANCE), min = Configuration.getInt(MIN_BALANCE);
            newProt = (TCProtocol) super.clone();
            newProt.neighborhood = new ArrayList();
            newProt.user = new NormalUser();
            newProt.user.addBitcoin((float) CommonState.r.nextInt(max - min) + min);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(TCProtocol.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return newProt;
    }

    @Override
    public void nextCycle(Node node, int i) {
        if(this.couldSpend() && Math.random() * 100 <= this.tx_prob)
            ((TCProtocol)node.getProtocol(i)).makeTransaction(i);
    }
    
    /**
     * 
     * @return true if the current user's balance is greater than 0.
     */
    public boolean couldSpend(){
        return this.user.getCurrentBalance() > 0;
    }
    
    @Override
    public void processEvent(Node node, int i, Object o) {
        TCMessage msg = (TCMessage) o;
        
        if(msg.isSingleTx){
            if(msg.content != null) this.addTransaction((Transaction)msg.content, node, i);
        }
        else{ 
            if(msg.content != null) this.addBlock((Block)msg.content, node, i);
        }
        
    }
    
    public void makeTransaction(int pid){
        //Chooses a random beneficiary and do a transaction. The node could choose also itself.
        TCProtocol beneficiary = (TCProtocol) Network.get(CommonState.r.nextInt(Network.size())).getProtocol(pid);

        Transaction newTx  = this.user.createTransaction(txMaxAmount, beneficiary.user);
        this.addTransaction(newTx, CommonState.getNode(), pid);
    }
    
    /**
     * Add a new transaction to the unconfirmed transaction, then propagate it to other nodes.
     * 
     * @param tx        the new transaction
     * @param currNode  the current node
     * @param pid       the TCProtocol pid 
     */
    public void addTransaction(Transaction tx, Node currNode, int pid){
        //Add new transaction to the set of unconfirmed ones. If the adding go well, propagate new transaction to the neighbors.
        if(this.user.recordNewTransaction(tx)){
            Linkable linkable = (Linkable) currNode.getProtocol(FastConfig.getLinkable(pid));
            TCMessage msg = new TCMessage(tx);
            
            for(int i = 0; i < linkable.degree(); i++){
                Node neig = linkable.getNeighbor(i);
                ((Transport)neig.getProtocol(FastConfig.getTransport(pid))).send(currNode, neig, msg, pid);
            }
        }
    }
    
    /**
     * Add a new block to the ledger, then propagate it to other nodes.
     * 
     * @param bl        the new block
     * @param currNode  the current node
     * @param pid       the TCProtocol pid 
     */
    public void addBlock(Block bl, Node currNode, int pid){
        //Add new transaction to the set of unconfirmed ones. 
        if(this.user.recordNewBlock(bl)){
            TCMessage msg = new TCMessage(bl);
            
            //If this node has also a mining protocol, send the message to it, otherwise propagates it on the network.
            if(((MiningProtocol)currNode.getProtocol(this.miningProtPid)).isMiner())
                ((Transport)currNode.getProtocol(FastConfig.getTransport(pid))).send(currNode, currNode, msg, this.miningProtPid);
            else{
                Linkable linkable = (Linkable) currNode.getProtocol(FastConfig.getLinkable(pid));
                for(int i = 0; i < linkable.degree(); i++){
                    Node neig = linkable.getNeighbor(i);
                    ((Transport)neig.getProtocol(FastConfig.getTransport(pid))).send(currNode, neig, msg, pid);
                }
            }
        }
    }
    
    /**
     * 
     * @return the user associated to this node
     */
    public TCUser getUser(){
        return this.user;
    }

    @Override
    public int degree() {
        return this.neighborhood.size();
    }

    @Override
    public Node getNeighbor(int i) {
        return this.neighborhood.get(i);
    }

    @Override
    public boolean addNeighbor(Node node) {
        if(this.neighborhood.contains(node)) return false;
        return this.neighborhood.add(node);
    }

    @Override
    public boolean contains(Node node) {
        return this.neighborhood.contains(node);
    }

    @Override
    public void pack() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onKill() {
        this.neighborhood.clear();
    }
}
