/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import tinycoin.Block;
import tinycoin.Transaction;

/**
 * The container for the messages exchanged by the nodes. It could contain a single transaction or a block. If a 
 * single transaction is contained, the value isSingleTx is setted to true.
 *
 * @author Giulio Auriemma
 */
public class TCMessage {
    public final boolean isSingleTx;
    public final Object content;
    
    public TCMessage(Object content){
        this.isSingleTx = content instanceof Transaction;
        this.content = content;
    }
}
