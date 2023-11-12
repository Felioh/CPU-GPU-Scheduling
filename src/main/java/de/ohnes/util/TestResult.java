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
    private double estimatedOptimum;
    private double achivedMakespan;
    private long milliseconds;

}
