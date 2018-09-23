/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tinycoin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import peersim.Simulator;
import utils.BarChartMaker;
import utils.XYChartMaker;

/**
 *
 * @author Giulio Auriemma
 */
public class TinyCoin {
    public static final String fileName = "\\normal";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulator.main(new String[]{System.getProperty("user.dir") + "\\config\\tc.config"});
        TinyCoin.readFile();
//        TinyCoin.readFile2();
    }
    
    private static void readFile(){
        try {
            int size = 1;
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "CPU"); put(2, "GPU"); put(3, "FPGA"); put(4, "ASIC");
//            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "5%"); put(2, "40%"); put(3, "65%"); put(4, "90%");
//            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "48x10^4"); put(2, "6x10^4"); put(3, "0,7x10^4");
//            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "ininfluente"); put(2, "medio"); put(3, "alto"); put(4, "estremo");
//            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "medesima potenza");
//            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "egoista"); put(2, "non egoista");
//            }};
            Map<Integer, String> fileMap = new HashMap(){{
                put(1, "normal"); put(2, "near future");
            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "base"); put(2, "passo 2"); put(3, "passo 3"); 
//            }};
            for(int i = 1; i <= size; i++){
                String name = fileMap.get(i);
                DataInputStream stream = new DataInputStream(new FileInputStream(new File(System.getProperty("user.dir") + "\\log\\data\\" + name + ".dat")));
                XYChartMaker.newDataSeries(name);
                while(stream.available() > 0){
                    final int x = stream.readInt();
                    stream.readChar();
                    final int y = stream.readInt();
                    stream.readChar();
                    XYChartMaker.addPoint(name, x, y);
                }
            }
            
            XYChartMaker.drowChart(System.getProperty("user.dir") + "\\log\\charts\\ESAME.png", "Simulazione standard", "Livello", "N° blocchi");
                
        } catch (IOException ex) {
            Logger.getLogger(TinyCoin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void readFile2(){
        try {
            int size = 3;
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "48x10^4"); put(2, "24x10^4"); put(3, "12x10^4"); put(4, "6x10^4");
//                put(5, "3x10^4"); put(6, "1,5x10^4"); put(7, "0,7x10^4"); put(8, "0,35x10^4");
//            }};
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "egoista"); put(2, "non egoista"); put(3, "egoista 80");
//            }};
//            for(int i = 1; i <= size; i++){
//                int minedBlock = 0, fork = 0;
//                String name = fileMap.get(i);
//                DataInputStream stream = new DataInputStream(new FileInputStream(new File(System.getProperty("user.dir") + "\\log\\data\\blocco6\\" + name + ".dat")));
//                while(stream.available() > 0){
//                    final int x = stream.readInt();
//                    stream.readChar();
//                    final int block = stream.readInt();
//                    minedBlock += block;
//                    fork += block - 1;
//                    stream.readChar();
//                }
//                BarChartMaker.addValue(minedBlock, "mined block", name);
//                BarChartMaker.addValue(fork, "fork", name);
//            }
            
//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "selfish"); put(2, "honest"); put(3, "normal1"); put(4, "normal2");
//            }};
//            DataInputStream stream = new DataInputStream(new FileInputStream(new File(System.getProperty("user.dir") + "\\log\\data\\blocco6\\egoismo.dat")));
//            for(int i = 1; i <= size; i++){
//                String name = fileMap.get(i);
//                int lvs = 0, blocks = 0, hblocks = 0;
//                
//                lvs = stream.readInt();
//                stream.readChar();
//                blocks = stream.readInt();
//                stream.readChar();
//                hblocks = stream.readInt();
//                stream.readChar();
//                
//                BarChartMaker.addValue(lvs, "levels", name);
//                BarChartMaker.addValue(blocks, "mined block", name);
//                BarChartMaker.addValue(blocks, "honest block", name);
//            }




//            Map<Integer, String> fileMap = new HashMap(){{
//                put(1, "normal"); put(2, "media"); put(3, "alta"); put(4, "alta7");
//            }};
            Map<Integer, String> fileMap = new HashMap(){{
                put(1, "base"); put(2, "passo 2"); put(3, "passo 3");
            }};
            for(int i = 1; i <= size; i++){
                int a = 0, b = 0, c = 0, d = 0;
                String name = fileMap.get(i);
                DataInputStream stream = new DataInputStream(new FileInputStream(new File(System.getProperty("user.dir") + "\\log\\data\\blocco7\\" + name + "bar.dat")));
                
                while(stream.available() > 0){
                    a = stream.readInt();
                    stream.readChar();
                    b = stream.readInt();
                    stream.readChar();
                    c = stream.readInt();
                    stream.readChar();
                    d = stream.readInt();
                    stream.readChar();
                }
                
                BarChartMaker.addValue(a, "tot non-sybil blocks", name);
                BarChartMaker.addValue(b, "non-sybil blocks", name);
                BarChartMaker.addValue(c, "tot sybil blocks", name);
                BarChartMaker.addValue(d, "sybil blocks", name);
            }
            
            
            BarChartMaker.drowChart(System.getProperty("user.dir") + "\\log\\charts\\tempobar.png", "Simulazione temporale", "Tempo trascorso", "N° blocchi");
                
        } catch (IOException ex) {
            Logger.getLogger(TinyCoin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
