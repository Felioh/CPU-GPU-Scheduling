package de.ohnes.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
// Math.floor
import java.lang.Math;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {
    
    private long id;
    private int startingTime;

    //moldable
    private int[] processingTimes;
    private int allotedMachines;
    
    //sequential
    private int sequentialProcessingTime;
    private Machine assignedMachine; //not really needed.
    
    public Job(int id, int[] processingTimes, int sequentialProcessingTime) {
        this.id = id;
        this.processingTimes = processingTimes;
        this.sequentialProcessingTime = sequentialProcessingTime;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Job && ((Job) obj).getId() == this.getId();
    }

    /**
     * 
     * @param i the amount of Machines [1..]
     * @return the execution time
     */
    public int getProcessingTime(int i) {
        return this.processingTimes[i - 1];
    }

    /**
     *
     * @return the sequential execution time
     */
    public int getSequentialProcessingTime() {
        return this.sequentialProcessingTime;
    }

    /**
     * find out the cononical number of machines for a job with max. execution time @param h.
     * using binary search
     * O(log m)
     * @return -1 if the job cant be executed in time h
     */
    public int canonicalNumberMachines(double h) {
        int r = processingTimes.length - 1;
        if(this.processingTimes[r] > h) {
            return -1;
        }
        int l = 0;
        while (r >= l) {
            int mid = l + (r - l) / 2;
            if (processingTimes[mid] == h) break;
            if (processingTimes[mid] < h) r = mid - 1;
            if (processingTimes[mid] > h) l = mid + 1;
        }
        return l + (r - l) / 2 + 1;
    }

    public void reset() {
        this.startingTime = 0;
        this.allotedMachines = 0;
    }

    /**
     * Calculate the "weight" of a job to classify its work on a sequential machine.
     * @param d the makespan guess
     * @return the weight
     */
    public int getSequentialWeight(double d) {
        int p = this.getSequentialProcessingTime();
        if(p > (2.0/3.0)*d) {
            return 2;
        } else if (p <= d/3.0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Round and scale the sequential processing time with rounding factor mu.
     * @param mu scaling factor
     * @return rounded and scaled processing time
     */
    public int getScaledRoundedSequentialProcessingTime(double mu) {
        return (int) Math.floor(mu * this.getSequentialProcessingTime());
    }
    
}
