package de.ohnes;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.ohnes.AlgorithmicComponents.Knapsack.MDKnapsack;
import de.ohnes.util.Job;
import de.ohnes.util.MDKnapsackChoice;
import de.ohnes.util.MDKnapsackItem;
import de.ohnes.util.Vector3D;

@RunWith(Parameterized.class)
public class MDKnapsackTests {

    private List<MDKnapsackItem> items;
    private Vector3D capacity;
    List<Job> shelf1;
    List<Job> shelf2;
    List<Job> smallJobs;
    List<Job> seqJobs;

    public MDKnapsackTests(List<MDKnapsackItem> items, Vector3D capacity, List<Job> shelf1, List<Job> shelf2, List<Job> smallJobs, List<Job> seqJobs) {
        super();
        this.capacity = capacity;
        this.items = items;
        this.seqJobs = seqJobs;
        this.shelf1 = shelf1;
        this.shelf2 = shelf2;
        this.smallJobs = smallJobs;
    }

    @Parameterized.Parameters
    public static List<Object[]> input() {

        //1st test instance.
        List<Job> shelf1 = new ArrayList<>();
        List<Job> shelf2 = new ArrayList<>();
        List<Job> smallJobs = new ArrayList<>();
        List<Job> seqJobs = new ArrayList<>();

        Vector3D capacity = new Vector3D(10, 10, 10);

        List<MDKnapsackItem> items = new ArrayList<>();
        MDKnapsackItem item1 = new MDKnapsackItem();
        item1.setJob(new Job(1, new int[]{10, 20, 30}, 20));
        item1.addChoice(MDKnapsackChoice.SHELF1, 1, new Vector3D(10, 0, 0));
        item1.addChoice(MDKnapsackChoice.SHELF2, 2, new Vector3D(0, 10, 0));
        item1.addChoice(MDKnapsackChoice.SEQUENCIAL, 3, new Vector3D(0, 0, 10));
        item1.addChoice(MDKnapsackChoice.SMALL, 4, new Vector3D(0, 0, 0));
        items.add(item1);

        MDKnapsackItem item2 = new MDKnapsackItem();
        item2.setJob(new Job(1, new int[]{10, 20, 30}, 20));
        item2.addChoice(MDKnapsackChoice.SHELF1, 1, new Vector3D(10, 0, 0));
        item2.addChoice(MDKnapsackChoice.SHELF2, 2, new Vector3D(0, 10, 0));
        item2.addChoice(MDKnapsackChoice.SEQUENCIAL, 3, new Vector3D(0, 0, 10));
        item2.addChoice(MDKnapsackChoice.SMALL, 4, new Vector3D(0, 0, 0));
        items.add(item2);

        MDKnapsackItem item3 = new MDKnapsackItem();
        item3.setJob(new Job(1, new int[]{10, 20, 30}, 20));
        item3.addChoice(MDKnapsackChoice.SHELF1, 1, new Vector3D(10, 0, 0));
        item3.addChoice(MDKnapsackChoice.SHELF2, 2, new Vector3D(0, 10, 0));
        item3.addChoice(MDKnapsackChoice.SEQUENCIAL, 3, new Vector3D(0, 0, 10));
        item3.addChoice(MDKnapsackChoice.SMALL, 4, new Vector3D(0, 0, 0));
        items.add(item3);


        return Arrays.asList(new Object[][] {{items, capacity, shelf1, shelf2, smallJobs, seqJobs}});
    }

    @Test
    public void testMDKnapsack() {
        MDKnapsack kS = new MDKnapsack();
        kS.solve(items, capacity, shelf1, shelf2, smallJobs, seqJobs);
        assertTrue("All jobs should be selected", shelf1.size() + shelf2.size() + smallJobs.size() + seqJobs.size() == items.size()); //length should be leq than capacity
        // assertThat(selectedJobs, );
    }
}
