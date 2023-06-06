package de.ohnes.util;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.ohnes.logger.InstanceDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = InstanceDeserializer.class)
@NoArgsConstructor
public class Instance {

    @JsonProperty("id")
    private long id = System.currentTimeMillis();
    @JsonProperty("number_jobs")
    private int n;
    @JsonProperty("moldable_machines")
    private int m;
    @JsonProperty("sequential_machines")
    private int l;
    // @JsonDeserialize(as = Job[].class)
    @JsonProperty("jobs")
    private Job[] jobs;

    @Setter
    @JsonIgnore
    private Machine[] machines = new Machine[0];

    public Instance(int n, int m, int l, Job[] jobs) {
        this.n = n;
        this.m = m;
        this.l = l;
        this.jobs = jobs;
    }

    
    /** 
     * @param minJobs
     * @param maxJobs
     * @param minMachines
     * @param maxMachines
     */
    public void generateRandomInstance(int minJobs, int maxJobs, int minMachines, int maxMachines, int maxSeqTime) {

        this.m = MyMath.getRandomNumber(minMachines, maxMachines);
        this.l = MyMath.getRandomNumber(minMachines, maxMachines); //TODO different parameters??
        this.n = MyMath.getRandomNumber(minJobs, maxJobs);
        this.jobs = new Job[this.n];
        
        for(int i = 0; i < this.n; i++) {
            int[] processingTimes = new int[this.m];
            processingTimes[0] = MyMath.getRandomNumber(20, maxSeqTime);
            for(int j = 1; j < this.m; j++) {
                processingTimes[j] = (int) (processingTimes[0] / (j + 1)); //minimal processing time
                // processingTimes[j] = MyMath.getRandomNumber((int) Math.ceil((j / (double) (j + 1)) * processingTimes[j - 1]), processingTimes[j - 1]); //comment in for random processing times.
            }
            int sequentialProcessingTime = processingTimes[0] + MyMath.getRandomNumber(20, maxSeqTime); //TODO!!
            this.jobs[i] = new Job(i, processingTimes, sequentialProcessingTime);
        }

    }

    @Override
    public String toString() {
        String result = "";
        result += "Machines: " + this.m + "\n";
        result += "Jobs:\n";
        for(Job j : this.jobs) {
            result += j.getId();
            result += "\t";
            for(double p : j.getProcessingTimes()) {
                result += p + ", ";
            }
            result += "\n";
        }
        return result;
    }

    @JsonIgnore
    public void addMachines(List<Machine> machines) {
        machines.addAll(Arrays.asList(this.machines));
        this.machines = machines.toArray(Machine[] :: new);
    }

    @JsonIgnore
    public double getMakespan() {
        double maxMakespan = 0;
        for(Job job : this.jobs) {
            double finishTime = job.getStartingTime() + job.getProcessingTime(job.getAllotedMachines());
            if(finishTime > maxMakespan) {
                maxMakespan = finishTime;
            }
        }
        return maxMakespan;
    }

    /**
     * return a job for i in [0 .. n]
     * @param i
     * @return
     */
    public Job getJob(int i) {
        return jobs[i];
    }

    /**
     * only for debugging
     * @return the total makespan
     */
    public double getMakespanBigJobs(double d) {
        double maxMakespan = 0;
        for(Job job : MyMath.findBigJobs(this, d)) {
            double finishTime = job.getStartingTime() + job.getProcessingTime(job.getAllotedMachines());
            if(finishTime > maxMakespan) {
                maxMakespan = finishTime;
            }
        }
        return maxMakespan;
    }

    public void resetInstance() {
        Arrays.asList(jobs).stream().forEach(j -> j.reset());
        this.machines = null;
    }
    
}
