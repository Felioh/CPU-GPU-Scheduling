package de.ohnes.AlgorithmicComponents.Shelves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ohnes.AlgorithmicComponents.LongestProcessingTime;
import de.ohnes.AlgorithmicComponents.Knapsack.MDKnapsack;
import de.ohnes.util.Job;
import de.ohnes.util.MDKnapsackChoice;
import de.ohnes.util.MDKnapsackItem;
import de.ohnes.util.Machine;
import de.ohnes.util.MyMath;
import de.ohnes.util.Vector3D;

/**
 * An implementation of the Algorithm by Mounie, Rapine, Trystram
 */
public class CpuGpuApproach extends GrageApproach {

    // invDelta = 1/\delta
    public static final int invDelta = 18;

    public CpuGpuApproach() {
        super();
    }

    /**
     * finds a two two shedule for the instance I with deadline d, if a schedule of length d exists.
     * @param d the deadline (makespan guess)
     * @param epsilon the "the small error"
     * @return true if a schedule of length d exists, false if none exists.
     */
    @Override
    public boolean solve(double d, double epsilon) {

        final int n = I.getN();
        final int l = I.getL();
        final int m = I.getM();
        final double v = (2.0 * invDelta) / d;
        final double mu = (1.0 * n * invDelta) / (d * l);

        List<Job> shelf2 = new ArrayList<>(Arrays.asList(MyMath.findBigJobs(I, d)));
        List<Job> smallJobs = new ArrayList<>(Arrays.asList(MyMath.findSmallJobs(I, d)));

        int totalWeight = 0; //variable to keep track of the total weight of all jobs.

        //transform to knapsack problem

        List<MDKnapsackItem> smallKnapsackItems = new ArrayList<>();
        List<MDKnapsackItem> bigKnapsackItems = new ArrayList<>();
        for (Job job : smallJobs) {
            MDKnapsackItem knapsackItem = new MDKnapsackItem();
            knapsackItem.setJob(job);
            //c_{i, S}
            knapsackItem.addChoice(MDKnapsackChoice.SMALL, job.getProcessingTime(1), new Vector3D(0, 0, 0));
            //c_{i, 3}
            int weight = 0;
            //if a choice would certainly violate the deadline d, we do not allow it.
            if (job.getSequentialProcessingTime() <= d) {
                weight = job.getSequentialWeight(d);
                knapsackItem.addChoice(MDKnapsackChoice.SEQUENTIAL, 0, new Vector3D(0, weight, job.getSequentialProcessingTime()));
            }
            totalWeight += weight;
            if (weight > 0) {
                // if the job is big
                bigKnapsackItems.add(knapsackItem);
            } else {
                // if the job is small
                smallKnapsackItems.add(knapsackItem);
            }
        }
        for (Job job : shelf2) {
            MDKnapsackItem knapsackItem = new MDKnapsackItem();
            knapsackItem.setJob(job);
            //c_{i, 1}
            int dAllotment = job.canonicalNumberMachines(d);
            //if a choice would certainly violate the deadline d, we do not allow it.
            if (dAllotment > 0) {
                knapsackItem.addChoice(MDKnapsackChoice.SHELF1, job.getProcessingTime(dAllotment) * dAllotment, new Vector3D(dAllotment, 0, 0));
            }
            //c_{i, 2}
            int dHalfAllotment = job.canonicalNumberMachines(d/2);
            //if a choice would certainly violate the deadline d, we do not alow it.
            //here, if there is no dHalfAllotment, then the canonical number of processors is set to be infinite,
            //thus the cost would be infinite, thus the resulting work would not be below the threshold.
            if (dHalfAllotment > 0) {
                knapsackItem.addChoice(MDKnapsackChoice.SHELF2, job.getProcessingTime(dHalfAllotment) * dHalfAllotment, new Vector3D(0, 0, 0));
            }
            //c_{i, 3}
            int weight = 0;
            //if a choice would certainly violate the deadline d, we do not allow it.
            if (job.getSequentialProcessingTime() <= d) {
                weight = job.getSequentialWeight(d);
                knapsackItem.addChoice(MDKnapsackChoice.SEQUENTIAL, 0, new Vector3D(0, weight, job.getSequentialProcessingTime()));
            }

            // if there is no valid choice for some job, then we must reject the deadline d.
            // this happens if a job can not be executed in time d on a sequential machine nor on m malleable machines.
            if (knapsackItem.getChoices().isEmpty()) {
                return false;
            }
            totalWeight += weight;
            if (weight > 0) {
                // if the job is big
                bigKnapsackItems.add(knapsackItem);
            } else {
                // if the job is small
                smallKnapsackItems.add(knapsackItem);
            }
        }
        

        // bigJobs = MyMath.dynamicKnapsack(bigJobs, weight, profit, bigJobs.length, I.getM(), I, d);
        MDKnapsack kS = new MDKnapsack();
        // int p1 = 0;
        List<Job> shelf1 = new ArrayList<>();
        shelf2.clear();
        List<Job> sequentialJobs = new ArrayList<>();
        smallJobs.clear();
        // 1st dimension: number of machines used by T_1 (less than m)
        // 2nd dimension: weight of tasks on L (less than 2l)
        // 3rd dimension: total work regarding the scaled and rounded instace on L (less than n/\delta)
        //      -> optimized: (less than 2l/\delta - \floor{2/(3\delta)}\sum_{i \in T_L} weight(i))
        Vector3D capacity = new Vector3D(m, 2 * l, 2 * l * invDelta); //integer division should take care of floor //TODO: restrict? - ((2 * invDelta) / 3) * totalWeight
        if (!kS.solve(smallKnapsackItems, bigKnapsackItems, capacity, shelf1, shelf2, smallJobs, sequentialJobs, mu, v)) {
            return false;   // if the knapsack problem is not solvable, then there is no schedule of length d.
        };

        //the knapsack problem should have used all jobs.
        assert shelf1.size() + shelf2.size() + smallJobs.size() + sequentialJobs.size() == n;

        // calculate the work for the jobs in the shelves for the malleable machines.
        double Ws = 0;
        double WShelf1 = 0;
        double WShelf2 = 0;
        int p1 = 0;
        for(Job job : smallJobs) {
            Ws += job.getProcessingTime(1);
        }
        for(Job job : shelf1) {
            int machines = job.canonicalNumberMachines(d);
            p1 += machines;
            job.setAllotedMachines(machines);
            WShelf1 += job.getAllotedMachines() * job.getProcessingTime(job.getAllotedMachines()); //update the work of shelf2
        }
        for(Job job : shelf2) {
            job.setAllotedMachines(job.canonicalNumberMachines(d/2));
            WShelf2 += job.getAllotedMachines() * job.getProcessingTime(job.getAllotedMachines()); //update the work of shelf2
        }

        if(WShelf1 + WShelf2 > m * d - Ws) {   //there cant exists a schedule of with makespan (s. Thesis Felix S. 76)
            return false;
        }

        // apply the applyTransformationRules
        List<Job> shelf0 = applyTransformationRules(d, shelf1, shelf2, p1);
        // List<Job> shelf0 = applyTransformationRules(d, shelf1, shelf2, p1);
        addSmallJobs(shelf1, shelf2, smallJobs, d, m);

        List<Machine> machinesS0 = new ArrayList<>();
        double startTime = -1;
        for(Job job : shelf0) {
            if(job.getStartingTime() != startTime) {
                Machine machine = new Machine(0);
                machine.addJob(job);
                machinesS0.add(machine);
                startTime = job.getProcessingTime(job.getAllotedMachines());
            } else {
                machinesS0.get(machinesS0.size() - 1).addJob(job);
                startTime += job.getProcessingTime(job.getAllotedMachines());
            }
        }
        I.addMachines(machinesS0);

        //use LPT on the sequential shelf to schedule those jobs
        LongestProcessingTime.LPT(sequentialJobs, I);

        return true;
    };

}
