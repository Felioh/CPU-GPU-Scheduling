package de.ohnes.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Setter;

@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@SuppressWarnings("unused")
public class TestResult {

    private long InstanceID;
    private int jobs;
    private int machines;
    private int seqMachines;
    private int malMachines;
    private double estimatedOptimum;
    private double achivedMakespan;
    private long milliseconds;

    public String toString() {
        return String.format("%d, %d, %d, %d, %d, %f, %f, %d", InstanceID, jobs, machines, seqMachines, malMachines, estimatedOptimum, achivedMakespan, milliseconds);
    }

}
