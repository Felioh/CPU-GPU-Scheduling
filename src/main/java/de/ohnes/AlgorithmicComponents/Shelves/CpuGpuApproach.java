package de.ohnes.AlgorithmicComponents.Shelves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ohnes.AlgorithmicComponents.Algorithm;
import de.ohnes.AlgorithmicComponents.Knapsack.MDKnapsack;
import de.ohnes.logger.printSchedule;
import de.ohnes.util.Instance;
import de.ohnes.util.Job;
import de.ohnes.util.MDKnapsackChoice;
import de.ohnes.util.MDKnapsackItem;
import de.ohnes.util.Machine;
import de.ohnes.util.MyMath;
import de.ohnes.util.Vector3D;
import lombok.NoArgsConstructor;

/**
 * An implementation of the Algorithm by Mounie, Rapine, Trystram
 */
@NoArgsConstructor
public class CpuGpuApproach implements Algorithm {

    protected Instance I;

    /**
     * finds a two two shedule for the instance I with deadline d, if a schedule of length d exists.
     * @param d the deadline (makespan guess)
     * @param epsilon the "the small error"
     * @return true if a schedule of length d exists, false if none exists.
     */
    public boolean solve(double d, double epsilon) {
        //"forget about small jobs"

        // inverted delta
        final int invDelta = 6;
        final int n = I.getJobs().length;
        final double mu = (1.0 * n * invDelta) / d;

        List<Job> shelf2 = new ArrayList<>(Arrays.asList(MyMath.findBigJobs(I, d)));
        List<Job> smallJobs = new ArrayList<>(Arrays.asList(MyMath.findSmallJobs(I, d)));

        //transform to knapsack problem

        List<MDKnapsackItem> knapsackItems = new ArrayList<>();
        for (Job job : smallJobs) {
            MDKnapsackItem knapsackItem = new MDKnapsackItem();
            //c_{i, S}
            knapsackItem.addChoice(MDKnapsackChoice.SMALL, job.getProcessingTime(1), new Vector3D(0, 0, 0));
            //c_{i, 3}
            //if a choice would certainly violate the deadline d, we do not allow it.
            if (job.getSequentialProcessingTime() <= d) {
                knapsackItem.addChoice(MDKnapsackChoice.SEQUENTIAL, 0, new Vector3D(0, job.getSequentialWeight(d), job.getScaledRoundedSequentialProcessingTime(mu)));
            }
            knapsackItems.add(knapsackItem);
        }
        for (Job job : shelf2) {
            MDKnapsackItem knapsackItem = new MDKnapsackItem();
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
            //if a choice would certainly violate the deadline d, we do not allow it.
            if (job.getSequentialProcessingTime() <= d) {
                knapsackItem.addChoice(MDKnapsackChoice.SEQUENTIAL, 0, new Vector3D(0, job.getSequentialWeight(d), job.getScaledRoundedSequentialProcessingTime(mu)));
            }

            // if there is no valid choice for some job, then we must reject the deadline d.
            // this happens if a job can not be executed in time d on a sequential machine nor on m malleable machines.
            if (knapsackItem.getChoices().isEmpty()) {
                return false;
            }
            knapsackItems.add(knapsackItem);
        }
        

        // bigJobs = MyMath.dynamicKnapsack(bigJobs, weight, profit, bigJobs.length, I.getM(), I, d);
        MDKnapsack kS = new MDKnapsack();
        // int p1 = 0;
        List<Job> shelf1 = new ArrayList<>();
        shelf2.clear();
        List<Job> sequentialJobs = new ArrayList<>();
        smallJobs.clear();
        Vector3D capacity = new Vector3D(I.getM(), 2* I.getL(), invDelta * I.getL()*I.getN());
        kS.solve(knapsackItems, capacity, shelf1, shelf2, smallJobs, sequentialJobs);

        // calculate the work for the jobs in the shelves for the malleable machines.
        double Ws = 0;
        double WShelf1 = 0;
        double WShelf2 = 0;
        for(Job job : smallJobs) {
            Ws += job.getProcessingTime(1);
        }
        for(Job job : shelf1) {
            job.setAllotedMachines(job.canonicalNumberMachines(d));
            WShelf1 += job.getAllotedMachines() * job.getProcessingTime(job.getAllotedMachines()); //update the work of shelf2
        }
        for(Job job : shelf2) {
            job.setAllotedMachines(job.canonicalNumberMachines(d/2));
            WShelf2 += job.getAllotedMachines() * job.getProcessingTime(job.getAllotedMachines()); //update the work of shelf2
        }

        if(WShelf1 + WShelf2 > I.getM() * d - Ws) {   //there cant exists a schedule of with makespan (s. Thesis Felix S. 76)
            return false;
        }

        // TODO: activate the applyTransformationRules
        // List<Job> shelf0 = applyTransformationRules(d, shelf1, shelf2, p1);
        addSmallJobs(shelf1, shelf2, smallJobs, d, I.getM());

        //TODO: use LPT on the sequential shelf to schedule those jobs

        return true;
    };

    protected void addSmallJobs(List<Job> shelf1, List<Job> shelf2, List<Job> smallJobs, double d, int availableMachines) {

        Machine[] machines = new Machine[availableMachines]; //keep track of the free time of each processor.
        int s1_i = 0;
        int s2_i = 0;
        int m1 = 0;
        int m2 = 0;
        for(int m = 0; m < availableMachines; m++) {
            machines[m] = new Machine(m);

            if(s1_i < shelf1.size()) {
                machines[m].addJob(shelf1.get(s1_i));
                if(m1 == 0) m1 = shelf1.get(s1_i).getAllotedMachines();
            } 
            if(--m1 == 0) {
                s1_i++;
            }

            if(s2_i < shelf2.size()) {
                //set starting time of the job from shelf 2.
                int startTime = (int) (3/2.0 * d) - shelf2.get(s2_i).getProcessingTime(shelf2.get(s2_i).getAllotedMachines());
                shelf2.get(s2_i).setStartingTime(startTime);
                machines[m].addJob(shelf2.get(s2_i));
                if(m2 == 0) m2 = shelf2.get(s2_i).getAllotedMachines();
            }
            if(--m2 == 0) {
                s2_i++;
            }

        }
        
        //allot small jobs.
        int i = 0;
        for(Job job : smallJobs) {
            while((3/2.0 * d) - machines[i].getUsedTime() < job.getProcessingTime(1)) { //should not be an infinite loop if "WShelf1 + WShelf2 <= I.getM() * d - Ws" -> (s. Thesis Felix p.78)
                if(i == machines.length - 1) {
                    i = 0;
                } else {
                    i++;
                }
            }
            job.setAllotedMachines(1);
            job.setStartingTime((int) machines[i].getFirstFreeTime(job.getProcessingTime(1)));
            machines[i].addJob(job);

        }

        this.I.setMachines(machines);
    }

    /**
     * converts a two shelf schedule to a feasible three schelves schedule. (s. 5.3.1.3 Thesis Felix)
     * @param I an Instance. A two Shelves schedule has to aleady been build.
     * @param d the deadline (also of the two shelves Schedule)
     * @return true if there exists a feasible schedule, false if not
     */
    protected List<Job> applyTransformationRules(double d, List<Job> shelf1, List<Job> shelf2, int p1) {

        List<Job> shelf0 = new ArrayList<>();
        int p0 = 0;     //processors required by S0.

        List<Job> jobsToDelete = new ArrayList<>();
        for(Job job : shelf2) {
            int q = I.getM() - (p1 + p0);
            if(q > 0 && job.getProcessingTime(q) <= (3/2.0) * d) {
                int p = job.canonicalNumberMachines((3/2.0) * d);
                job.setAllotedMachines(p);      //either S0 or S1.
                jobsToDelete.add(job);
                if(job.getProcessingTime(p) > d) {
                    p0 += p;
                    shelf0.add(job);
                } else {
                    p1 += p;
                    shelf1.add(job);
                }
            }
        }
        shelf2.removeAll(jobsToDelete);

        Job singleSmallJob = null;
        int i = 0;
        while(i < shelf1.size()) {
            Job job = shelf1.get(i);
            int allotedMachines = job.getAllotedMachines();
            int pTime = job.getProcessingTime(allotedMachines);
            
            if (pTime <= (3/4.0) * d && allotedMachines > 1) {
                p1 -= job.getAllotedMachines();
                job.setAllotedMachines(allotedMachines - 1);        //assign to shelf 0.
                shelf0.add(job);
                shelf1.remove(job);
                p0 += job.getAllotedMachines();
                continue;
            } else if(pTime <= (3/4.0) * d && allotedMachines == 1) {
                if(singleSmallJob == null) {
                    singleSmallJob = job;
                } else {
                    p1 -= job.getAllotedMachines();     // == 1
                    p1 -= singleSmallJob.getAllotedMachines();
                    p0 += job.getAllotedMachines();
                    singleSmallJob.setStartingTime(pTime);         //assign both to shelf 0.
                    shelf0.add(job);
                    shelf0.add(singleSmallJob);
                    shelf1.remove(job);
                    shelf1.remove(singleSmallJob);
                    singleSmallJob = null;
                    continue;
                }
            }
            i++;
        }

        return shelf0;

    }

    @Override
    public void setInstance(Instance I) {
        this.I = I;
        
    }

}
