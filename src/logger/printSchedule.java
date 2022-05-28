package logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import util.Job;

public class printSchedule {

    public static String printScheduledJobs(Job[] jobs) {
        String result = "";
        boolean flipper = false;
        for(Job job : jobs) {
            //result += String.format("Job: %03d", job.getId()) + "-".repeat(100) + "\n";
            for(int m = 0; m < job.getAllotedMachines(); m++) {
                result += getLineString(job.getId(), job.getProcessingTime(job.getAllotedMachines()), flipper ? "#" : "/");
                // if (flipper) result += "#".repeat(job.getProcessingTime(job.getAllotedMachines())) + "\n";
                // else result += "/".repeat(job.getProcessingTime(job.getAllotedMachines())) + "\n";
            }
            flipper = !flipper;
        }
        return result;
    }

    private static String getLineString(long JobID, int length, String character) {
        return String.format(character.repeat(length / 2 - 2) + "%03d" + character.repeat(length / 2 - 2), JobID) + "\n";
    }


    public static String printTwoShelves(Job[] jobs, int d) {
        String result = "";
        boolean flipper = false;

        List<Job> shelf2 = Arrays.stream(jobs).filter(j -> j.getProcessingTime(j.getAllotedMachines()) <= d/2).collect(Collectors.toList());
        List<Job> shelf1 = Arrays.stream(jobs).filter(j -> !shelf2.contains(j)).collect(Collectors.toList());
        result += "Shelf 1:\n";
        for(Job job : shelf1) {
            //result += String.format("Job: %03d", job.getId()) + "-".repeat(100) + "\n";
            for(int m = 0; m < job.getAllotedMachines(); m++) {
                result += getLineString(job.getId(), job.getProcessingTime(job.getAllotedMachines()), flipper ? "#" : "/");
                // if (flipper) result += "#".repeat(job.getProcessingTime(job.getAllotedMachines())) + "\n";
                // else result += "/".repeat(job.getProcessingTime(job.getAllotedMachines())) + "\n";
            }
            flipper = !flipper;
        }
        result += "Shelf 2:\n";
        for(Job job : shelf2) {
            //result += String.format("Job: %03d", job.getId()) + "-".repeat(100) + "\n";
            for(int m = 0; m < job.getAllotedMachines(); m++) {
                result += getLineString(job.getId(), job.getProcessingTime(job.getAllotedMachines()), flipper ? "#" : "/");
                // if (flipper) result += "#".repeat(job.getProcessingTime(job.getAllotedMachines())) + "\n";
                // else result += "/".repeat(job.getProcessingTime(job.getAllotedMachines())) + "\n";
            }
            flipper = !flipper;
        }
        return result;
    }
    
}