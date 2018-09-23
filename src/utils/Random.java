/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import network.DataMinerFile;
import peersim.config.Configuration;
import tinycoin.MinerUser;
import network.MiningStrategy;
import tinycoin.TCUser;

/**
 * A singleton class that expose some method for probability computation.
 * 
 * @author Giulio Auriemma
 */
public class Random {
    /**
     * The singleton instance.
     */
    public static final Random RAND = new Random();
    public final Prover PROVER;
    
    private static final String SUFFIX = "TCRandom", MINER_DISTR_STR = "distr.minertype", PROOF_OF_WORK_DIFF = "powdiff",
        SELFISH_PROB = "mining.selfish.prob", SELFISH_GREED = "mining.selfish.greed", SELFISH_POWER = "mining.selfish.power",
        SYBIL_PROB = "mining.sybil.prob", SYBIL_GREED = "mining.sybil.greed", SYBIL_POWER = "mining.sybil.power";
    private static final String []MINER_TYPE = new String[]{"CPU", "GPU", "FPGA", "ASIC"};
    private final int MINER_TYPE_DISTR[];
    private final Parameters SELFISH_PARAMS, SYBIL_PARAMS;
    
    private Random(){
        this.MINER_TYPE_DISTR = new int[]{Configuration.getInt(SUFFIX + "." + MINER_DISTR_STR + ".cpu"),
                                            Configuration.getInt(SUFFIX + "." + MINER_DISTR_STR + ".gpu"), 
                                            Configuration.getInt(SUFFIX + "." + MINER_DISTR_STR + ".fpga"),
                                            Configuration.getInt(SUFFIX + "." + MINER_DISTR_STR + ".asic")};
        this.SELFISH_PARAMS = new Parameters(Configuration.getInt(SUFFIX + "." + SELFISH_PROB), 
                                         Configuration.getInt(SUFFIX + "." + SELFISH_GREED),
                                         Configuration.getString(SUFFIX + "." + SELFISH_POWER));
        this.SYBIL_PARAMS = new Parameters(Configuration.getInt(SUFFIX + "." + SYBIL_PROB), 
                                         Configuration.getInt(SUFFIX + "." + SYBIL_GREED),
                                         Configuration.getString(SUFFIX + "." + SYBIL_POWER));
        this.PROVER = new Prover(Configuration.getInt(SUFFIX + "." + PROOF_OF_WORK_DIFF));
    }
    
    /**
     * Return a random miner instance and a random mining strategy. The choise is not making completely at random, since it
     * is also based on the random parameters of configuration file. 
     * 
     * @return a random miner instance and a random mining strategy
     */
    public MinerCharacteristics getRandomMinerChar(){
        double rand = Math.random() * 100;
        
        if(rand <= this.SELFISH_PARAMS.prob){
            String type = this.SELFISH_PARAMS.pow.equals("RAND") ? 
                Random.<String>chooseObjByDistribution(MINER_TYPE_DISTR, Random.MINER_TYPE)
                : this.SELFISH_PARAMS.pow;
            byte greed = this.SELFISH_PARAMS.greed >= 0 ?
                (byte)this.SELFISH_PARAMS.greed : (byte)(Math.random() * 100);
//            MinerUser miner = new MinerUser(Enum.valueOf(TCUser.MinerType.class, type), greed);
//            DataMinerFile.hminers.add(miner);
            return new MinerCharacteristics(new MinerUser(Enum.valueOf(TCUser.MinerType.class, type), greed), 
                                            MiningStrategiesFactory.getMiningStrategy(MiningStrategiesFactory.SELFISH));
        }
        else if(rand <= this.SELFISH_PARAMS.prob + this.SYBIL_PARAMS.prob){
            String type = this.SYBIL_PARAMS.pow.equals("RAND") ? 
                Random.<String>chooseObjByDistribution(MINER_TYPE_DISTR, Random.MINER_TYPE)
                : this.SYBIL_PARAMS.pow;
            byte greed = this.SYBIL_PARAMS.greed >= 0 ?
                (byte)this.SYBIL_PARAMS.greed : (byte)(Math.random() * 100);
            
            return new MinerCharacteristics(new MinerUser(Enum.valueOf(TCUser.MinerType.class, type), greed), 
                                            MiningStrategiesFactory.getMiningStrategy(MiningStrategiesFactory.SYBIL));
        }
        
//        MinerUser miner = this.getRandomMiner();
//        DataMinerFile.hminers.add(miner);
        return new MinerCharacteristics(this.getRandomMiner(),
                MiningStrategiesFactory.getMiningStrategy(MiningStrategiesFactory.HONEST));
        

//        int honestProb = 100 - this.SELFISH_PARAMS.prob - this.SYBIL_PARAMS.prob;
//        return Random.<MinerCharacteristics>chooseObjByDistribution(
//                new int[]{this.SELFISH_PARAMS.prob, this.SYBIL_PARAMS.prob, honestProb}, 
//                new MinerCharacteristics[]{
//                    new MinerCharacteristics(new MinerUser(Enum.valueOf(TCUser.MinerType.class, 
//                                this.SELFISH_PARAMS.pow.equals("RAND") ? 
//                                Random.<String>chooseObjByDistribution(MINER_TYPE_DISTR, Random.MINER_TYPE)
//                                : this.SELFISH_PARAMS.pow), 
//                            this.SELFISH_PARAMS.greed >= 0 ?
//                            (byte)this.SELFISH_PARAMS.greed : (byte)(Math.random() * 100)), 
//                        MiningStrategiesFactory.getMiningStrategy(MiningStrategiesFactory.SELFISH)),
//                    new MinerCharacteristics(new MinerUser(Enum.valueOf(TCUser.MinerType.class, 
//                                this.SYBIL_PARAMS.pow.equals("RAND") ? 
//                                Random.<String>chooseObjByDistribution(MINER_TYPE_DISTR, Random.MINER_TYPE)
//                                : this.SYBIL_PARAMS.pow), 
//                            this.SYBIL_PARAMS.greed >= 0 ?
//                            (byte)this.SYBIL_PARAMS.greed : (byte)(Math.random() * 100)), 
//                        MiningStrategiesFactory.getMiningStrategy(MiningStrategiesFactory.SELFISH)),
//                    new MinerCharacteristics(this.getRandomMiner(),
//                        MiningStrategiesFactory.getMiningStrategy(MiningStrategiesFactory.HONEST))
//                });
    }
    
    /**
     * Returns a miner with random parameters.
     * 
     * @return the new random miner.
     */
    public MinerUser getRandomMiner(){
        return new MinerUser(Enum.valueOf(TCUser.MinerType.class, Random.<String>chooseObjByDistribution(MINER_TYPE_DISTR, 
                new String[]{"CPU", "GPU", "FPGA", "ASIC"})), (byte) (Math.random() * 100));
    }
    
    /**
     * Given a probability distribution and an array of object, the function chooses one of the object at random. 
     * The object with probability distr[i] to be chosen is in position i of the array objs. The length of the prob distr
     * array must be lesser or equal to the one of the array of object.
     * 
     * @param <T>       the type of the object
     * @param distr     the probability distribution
     * @param objs      the array of objects
     * @return          the chosen object, null if there is an error
     */
    @SuppressWarnings("empty-statement")
    public static <T> T chooseObjByDistribution(int []distr, T []objs){
        if(distr.length == 0 || distr.length < objs.length)  return null;
        
        int  acc = 100, i;
        double rand = Math.floor(Math.random() * 100);
        
        for(i = distr.length - 1; i > 0 && (acc -= distr[i]) > rand; i--) ;
        
        return objs[i];
    }
    
    /**
     * A container for the miner characteristics: the MinerUser object and the mining strategy.
     */
    public static class MinerCharacteristics{
        private final MinerUser miner;
        private final MiningStrategy strategy;
        
        private MinerCharacteristics(MinerUser miner, MiningStrategy strategy){
            this.miner = miner;
            this.strategy = strategy;
        }
        
        /**
         * 
         * @return the miner.
         */
        public MinerUser getMiner(){
            return this.miner;
        }
        
        /**
         * 
         * @return the mining strategy.
         */
        public MiningStrategy getStrategy(){
            return this.strategy;
        }
    }
    
    /**
     * A container for mining strategy parameters.
     */
    private static class Parameters{
        private final int prob, greed;
        private final String pow;
        
        private Parameters(int prob, int greed, String pow){
            this.prob = prob;
            this.greed = greed;
            this.pow = pow;
        }
    }
    
    /**
     * The class that executes the proof of work.
     */
    public static class Prover{
        private static final long POW2_32 = (long)Math.pow(2, 32), POW10_6 = (long)Math.pow(10, 9);
        public int cycle = 0, diff;
        
        private Prover(int diff){
            this.diff = diff;
        };
        
        public void nextCycle(){ this.cycle++; }
        
        /**
         * Simulate a proof of work by two step probabilistic computation.
         * 
         * @param power the mining power of a 
         * @return 
         */
        public boolean proofOfWork(double power){
            double time = (this.diff * POW2_32) / (power * POW10_6 ),
                    prob = this.cycle / time;
            if(Math.random() * 100 <= prob){
                    this.cycle = 1;
                    return true;
            }
            return false;
        }
        
        /**
         * Round a double up to decimalLength decimal digit.
         * 
         * @param num               the double to round
         * @param decimalLength     the desidered number of decimal digit
         * @return                  the double approximated
         */
        private double round(double num, int decimalLength){
            double app = Math.pow(10, decimalLength);
            return (double) Math.round(num * app) / app;
        }
    }
}
