package de.ohnes;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.ohnes.AlgorithmicComponents.LongestProcessingTime;
import de.ohnes.util.Instance;
import de.ohnes.util.Job;
import de.ohnes.util.Machine;
/**
 * Tests for the complete program.
 * 
 * tests all predefined instances from the TestInstances Folder as well as some randomly generated ones.
 */
@RunWith(Parameterized.class)
public class LPTTests {

    private Instance I;

    /**
     * generate a Random Instance an solve it.
     */
    public LPTTests(Instance I) {
        this.I = I;
    }

    /**
     * Test all test-Instances from the folder TestInstances
     * and some randomly generated Isntances.
     * @return
     */
    @Parameterized.Parameters
    public static List<Object[]> input() {
        List<Instance[]> instances = new ArrayList<>();

        File dir = new File("TestInstances");
        File[] files = dir.listFiles();
        if(files != null) {
            for(File testFile : files) {
                Instance[] args = new Instance[1];
                try {
                    args[0] = new ObjectMapper().readValue(testFile, Instance.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                instances.add(args);
            }
        }

        for(int i = 0; i < 20; i++) {
            Instance[] args = new Instance[1];
            args[0] = new Instance(0, 0, 0, null);
            args[0].generateRandomInstance(100, 1000, 10, 50, 1, 100);
            instances.add(args);
        }
        

        return Arrays.asList(instances.toArray(Object[][] :: new));
    }

    @Test
    public void testLPT() {
        LongestProcessingTime.LPT(Arrays.asList(this.I.getJobs()), this.I);
        assertTrue("all jobs are assigned to a machine", Arrays.asList(this.I.getJobs()).stream().allMatch(j -> j.getAssignedMachine() != null));
        
        int smallestProcessingTime = Arrays.asList(this.I.getJobs()).stream().sorted(Comparator.comparing(Job::getSequentialProcessingTime)).findFirst().get().getSequentialProcessingTime();
        int biggestMakespan = (int) Arrays.asList(this.I.getMachines()).stream().sorted(Comparator.comparing(Machine::getUsedTime)).findFirst().get().getUsedTime();
        int smallestMakespan = (int) Arrays.asList(this.I.getMachines()).stream().sorted(Comparator.comparing(Machine::getUsedTime).reversed()).findFirst().get().getUsedTime();

        assertTrue("The load difference between the biggest and smallest load should be smaller than the smallest processing time", smallestProcessingTime >= (biggestMakespan - smallestMakespan));

        
    }

}
