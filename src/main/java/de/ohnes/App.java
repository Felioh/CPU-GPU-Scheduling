package de.ohnes;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.ohnes.AlgorithmicComponents.Approximation.TrivialLowerBound;
import de.ohnes.AlgorithmicComponents.Shelves.CpuGpuApproach;
import de.ohnes.logger.MyElasticsearchClient;
import de.ohnes.logger.printSchedule;
import de.ohnes.util.Instance;
import de.ohnes.util.TestResult;

public class App {

    private static final Logger LOGGER = LogManager.getLogger(App.class);

    private static Integer nbTests;
    private static Double epsilon;
    private static Integer minJobs;
    private static Integer maxJobs;
    private static Integer minMachines;
    private static Integer maxMachines;
    private static Double seqMachines;
    private static Integer maxSeqTime;
    private static String ESHost;
    private static int ESPort;
    private static String ESIndex;
    private static String TestFilePath;
    
    /** 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configurator.setRootLevel(Level.ALL);
        readEnv();
        
        LOGGER.info("Starting Algorithm!");
        MyElasticsearchClient.makeConnection(ESHost, ESPort);

        // read test files if specified
        if (TestFilePath != null) {
            File dir = new File(TestFilePath);
            File[] files = dir.listFiles();
            if(files != null) {
                for(File testFile : files) {
                    Instance I = new Instance();
                    try {
                        I = new ObjectMapper().readValue(testFile, Instance.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LOGGER.info("Reading test file from {}/{}", TestFilePath, testFile.getName());
                    TestResult tr = runTest(I);
                    LOGGER.info("Computed Schedule: \n{}", printSchedule.printMachines(I.getMachines()));
                    MyElasticsearchClient.pushData(ESIndex, tr);
                }
            }
        } else {

            try {
                for (int i = 0; i < nbTests; i++) {
                    Instance I = new Instance();
                    I.generateRandomInstance(minJobs, maxJobs, minMachines, maxMachines, seqMachines, maxSeqTime);
                    MyElasticsearchClient.pushData(ESIndex, runTest(I));
                }
            } catch (OutOfMemoryError e) {
                LOGGER.error("Out of Memory Error. Exiting...");
                System.exit(1);
            }
        }
        System.exit(0);
    }

    private static void readEnv() {
        nbTests = Integer.parseInt(System.getenv("NB_TESTS"));
        epsilon = Double.parseDouble(System.getenv("EPSILON"));
        minJobs = Integer.parseInt(System.getenv("INSTANCE_MINJOBS"));
        maxJobs = Integer.parseInt(System.getenv("INSTANCE_MAXJOBS"));
        minMachines = Integer.parseInt(System.getenv("INSTANCE_MINMACHINES"));
        maxMachines = Integer.parseInt(System.getenv("INSTANCE_MAXMACHINES"));
        seqMachines = Double.parseDouble(System.getenv("INSTANCE_SEQMACHINES"));
        maxSeqTime = Integer.parseInt(System.getenv("INSTANCE_MAX_SEQUENTIAL_TIME"));
        ESHost = System.getenv("ES_HOST");
        ESPort = Integer.parseInt(System.getenv("ES_PORT"));
        ESIndex = System.getenv("ES_INDEX");
        TestFilePath = System.getenv("TEST_FILE_PATH");
    }
    
    /** 
     * @return TestResult
     */
    private static TestResult runTest(Instance I) {

        DualApproximationFramework dF = new DualApproximationFramework(null, new CpuGpuApproach(), new TrivialLowerBound(), I);


        long startTime = System.currentTimeMillis();
        double d = dF.start(epsilon);
        long endTime = System.currentTimeMillis();
        LOGGER.info("Ran instance with {} malleable, {} sequential machines and {} jobs in {} milliseconds.", I.getM(), I.getL(), I.getN(), (endTime - startTime));

        TestResult tr = new TestResult();
        tr.setAchivedMakespan(I.getMakespan());
        tr.setEstimatedOptimum(d);
        tr.setJobs(I.getN());
        tr.setMachines(I.getM() + I.getL());
        tr.setMalMachines(I.getM());
        tr.setSeqMachines(I.getL());
        tr.setMilliseconds((endTime - startTime));
        tr.setInstanceID(I.getId());

        LOGGER.info("Result: {}", tr.toString());
        return tr;
    }
}
