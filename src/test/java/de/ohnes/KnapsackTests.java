package de.ohnes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.ohnes.AlgorithmicComponents.Knapsack.DynamicKnapsack;
import de.ohnes.AlgorithmicComponents.Knapsack.KnapsackSolver;
import de.ohnes.util.Job;

@RunWith(Parameterized.class)
public class KnapsackTests {

    private List<Job> allJobs;
    private int[] wt;
    private int[] val;
    private int n;
    private int W;

    public KnapsackTests(List<Job> allJobs, int[] wt, int[] val, int n, int W) {
        super();
        this.allJobs = allJobs;
        this.wt = wt;
        this.val = val;
        this.n = n;
        this.W = W;
    }

    @Parameterized.Parameters
    public static List<Object[]> input() {

        //1st test env
        List<Job> allJobs1 = new ArrayList<>();
        int[] wt1 = {1, 1, 1, 1, 2, 2, 2, 2};
        int[] val1 = {2, 4, 6, 8, 3, 6, 9, 12};
        int n1 = 8;
        int W1 = 3;
        for(int i = 0; i < n1; i++) {
            allJobs1.add(new Job(i, null, 0));
        }

        return Arrays.asList(new Object[][] {{allJobs1, wt1, val1, n1, W1}});
    }

    @Test
    public void KnapsackTestDynamicOutputSize() {
        KnapsackSolver kS = new DynamicKnapsack();
        List<Job> selectedJobs = kS.solve(allJobs, wt, val, n, W);
        assertTrue("The number of selected Jobs should be <= to the capacity", selectedJobs.size() <= W); //length should be leq than capacity
        // assertThat(selectedJobs, );
    }
    
    @Test
    public void testOptimalValue() {
        KnapsackSolver kS = new DynamicKnapsack();
        List<Job> selectedJobs = kS.solve(allJobs, wt, val, n, W);
        int totalValue = selectedJobs.stream().mapToInt(j -> val[(int) j.getId()]).sum();
        int totalWeight = selectedJobs.stream().mapToInt(j -> wt[(int) j.getId()]).sum();
        assertTrue("The total weight should be <= to the capacity", totalWeight <= W);
        assertEquals("The total value should be optimal", 20, totalValue);
    }
    
}
