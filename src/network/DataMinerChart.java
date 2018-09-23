/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import tinycoin.BlockChain;
import utils.XYChartMaker;

/**
 *
 * @author Giulio Auriemma
 */
public class DataMinerChart implements Control{
    private static final String TC_PROT_PID = "tc", SIMULATION_NAME = "name";
    private final int tcProtPid;
    private final String simName;
    private boolean isFirstTime = true;
    
    public DataMinerChart(String suffix){
        this.tcProtPid = Configuration.getPid(suffix + "." + TC_PROT_PID);
        this.simName = XYChartMaker.newDataSeries(Configuration.getString(suffix + "." + SIMULATION_NAME));
    }
    
    @Override
    public boolean execute() {
        if(!isFirstTime){
            final TCProtocol ex = (TCProtocol) Network.get(CommonState.r.nextInt(Network.size())).getProtocol(this.tcProtPid);
            final BlockChain ledger = ex.getUser().getLedger();

            for (int i = 0; i < ledger.getSize(); i++){
                final int size = ledger.getLevel(i).size();
                
                XYChartMaker.addPoint(this.simName, i, size);
            }
        }
        this.isFirstTime = false;
        return false; 
    }
}
