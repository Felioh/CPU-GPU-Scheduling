package de.ohnes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.runners.Parameterized;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.ohnes.AlgorithmicComponents.Approximation.TwoApproximation;
import de.ohnes.AlgorithmicComponents.Shelves.CpuGpuApproach;
import de.ohnes.util.Instance;

public class CPUGPUTests {
    
    private Instance I;
    private double d;

    /**
     * generate a Random Instance an solve it.
     */
    public CPUGPUTests(Instance I) {
        this.I = I;
        DualApproximationFramework dualApproxFramework = new DualApproximationFramework(null, new CpuGpuApproach(), new TwoApproximation(), I); //todo constant approx.
        this.d = dualApproxFramework.start(0.1);
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
            args[0].generateRandomInstance(100, 1000, 10, 50, 100);
            instances.add(args);
        }
        

        return Arrays.asList(instances.toArray(Object[][] :: new));
    }
}
