package de.ohnes;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import de.ohnes.AlgorithmicComponents.Approximation.TwoApproximation;
import de.ohnes.AlgorithmicComponents.Shelves.CpuGpuApproach;
import de.ohnes.logger.MyElasticsearchClient;
import de.ohnes.util.Instance;
import de.ohnes.util.TestResult;

public class App {

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private static Double epsilon;
    private static Integer minJobs;
    private static Integer maxJobs;
    private static Integer minMachines;
    private static Integer maxMachines;
    private static Integer maxSeqTime;
    private static String ESHost;
    private static String ESIndex;
    
    /** 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.ALL);

        epsilon = Double.parseDouble(System.getenv("EPSILON"));
        minJobs = Integer.parseInt(System.getenv("INSTANCE_MINJOBS"));
        maxJobs = Integer.parseInt(System.getenv("INSTANCE_MAXJOBS"));
        minMachines = Integer.parseInt(System.getenv("INSTANCE_MINMACHINES"));
        maxMachines = Integer.parseInt(System.getenv("INSTANCE_MAXMACHINES"));
        maxSeqTime = Integer.parseInt(System.getenv("INSTANCE_MAX_SEQUENTIAL_TIME"));
        ESHost = System.getenv("ES_HOST");
        ESIndex = System.getenv("ES_INDEX");
        
        LOGGER.info("Starting Algorithm!");
        MyElasticsearchClient.makeConnection(ESHost);
        try {
            while(true) {
                MyElasticsearchClient.pushData(ESIndex, runTest());
            }
        } catch (OutOfMemoryError e) {
            LOGGER.error("Out of Memory Error. Exiting...");
            System.exit(1);
        }

    }

    
    /** 
     * @return TestResult
     */
    private static TestResult runTest() {
        Instance I = new Instance();
        I.generateRandomInstance(minJobs, maxJobs, minMachines, maxMachines, maxSeqTime);

        DualApproximationFramework dF = new DualApproximationFramework(null, new CpuGpuApproach(), new TwoApproximation(), I);


        long startTime = System.currentTimeMillis();
        double d = dF.start(epsilon);
        long endTime = System.currentTimeMillis();
        LOGGER.info("Ran instance with {} machines and {} jobs in {} milliseconds.", I.getM(), I.getN(), (endTime - startTime));

        TestResult tr = new TestResult();
        tr.setAchivedMakespan(I.getMakespan());
        tr.setEstimatedOptimum(d);
        tr.setJobs(I.getN());
        tr.setMachines(I.getM());
        tr.setMilliseconds((endTime - startTime));
        tr.setInstanceID(I.getId());

        return tr;
    }

}
