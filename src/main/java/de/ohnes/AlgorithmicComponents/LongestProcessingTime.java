package de.ohnes.AlgorithmicComponents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.ohnes.util.Instance;
import de.ohnes.util.Job;
import de.ohnes.util.Machine;

public class LongestProcessingTime {
    
    /**
     * naive longest-processing time schedule for all jobs and machines in @I
    */
    public static void LPT(List<Job> seqJobs, Instance I) {
        //TODO implement a soring algorithm, or check performance of this.
        List<Job> jobs = seqJobs.stream().sorted(Comparator.comparing(Job::getSequentialProcessingTime).reversed()).collect(Collectors.toList());


        //TODO: use heap for machines??
        List<Machine> machines = new ArrayList<>();
        for (int i = 0; i < I.getL(); i++) {
            machines.add(new Machine(i));
        }

        for (Job job : jobs) {
            Machine machine = machines.stream().sorted(Comparator.comparing(Machine::getUsedTime)).findFirst().get(); //TODO do not sort the whole list again.
            job.setStartingTime((int) machine.getUsedTime());   //assuming that all jobs are scheduled that way.
            job.setAllotedMachines(-1);
            job.setAssignedMachine(machine);
            machine.addJob(job);
        }

        I.addMachines(machines);

    }
}
