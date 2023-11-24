package de.ohnes.AlgorithmicComponents.Knapsack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.ohnes.util.Job;
import de.ohnes.util.KnapsackChoice;
import de.ohnes.util.MDKnapsackItem;
import de.ohnes.util.Vector3D;

/**
 * The MDKnapsack class represents a multi-dimensional knapsack problem solver.
 * It provides a method to solve the problem and allocate jobs to different shelves based on their weights and costs.
 */
public class MDKnapsack {
    /**
     * solves a multi-dimensional knapsack problem.
     * 
     * @param smallItems
     * @param bigItems
     * @param capacity
     * @param shelf1
     * @param shelf2
     * @param smallJobs
     * @param seqJobs
     */
    public void solve(List<MDKnapsackItem> smallItems, List<MDKnapsackItem> bigItems, Vector3D capacity, List<Job> shelf1, List<Job> shelf2, List<Job> smallJobs, List<Job> seqJobs) {

        int b = bigItems.size();
        int s = smallItems.size();
        int n = s + b;
        //TODO: reduce 3rd dimension
        Double[][][][] dp = new Double[b+1][capacity.get(0)+1][capacity.get(2)+1][capacity.get(1)+1];
        
        
        //initialization
        for (int x1 = 0; x1 < dp[0].length; x1++) {
            for (int x2 = 0; x2 < dp[0][x1].length; x2++) {
                for (int x3 = 0; x3 < dp[0][x1][x2].length; x3++) {
                    dp[0][x1][x2][x3] = 0.0;
                }
            }
        }
        
        //acutal dp
        // fist solve the knapsack problem for the big items
        for (int i = 1; i <= bigItems.size(); i++) {
            Integer[] costs = bigItems.get(i-1).getCosts();
            Vector3D[] weights = bigItems.get(i-1).getWeights();
            for (int x1 = 0; x1 < dp[0].length; x1++) {
                for (int x2 = 0; x2 < dp[0][x1].length; x2++) {
                    for (int x3 = 0; x3 < dp[0][x1][x2].length; x3++) {
                        double minVal = Double.MAX_VALUE;
                        for (int c = 0; c < costs.length; c++) { //for the choices
                            Vector3D w = weights[c];
                            int x1_ = x1 - w.get(0);
                            int x2_ = x2 - w.get(2);
                            int x3_ = x3 - w.get(1);
                            if (x1_ < 0 || x2_ < 0 || x3_ < 0) {
                                continue;
                            }
                            if (dp[i-1][x1_][x2_][x3_] == null) {
                                continue;
                            }
                            if (dp[i-1][x1_][x2_][x3_] + costs[c] < minVal) {
                                minVal = dp[i-1][x1_][x2_][x3_] + costs[c];
                            }
                        }
                        if (minVal < Double.MAX_VALUE) {
                            dp[i][x1][x2][x3] = minVal;
                        }
                    }
                }
            }
        }

        //discard 2nd constraint, as the small items don't change it.
        
        Double[][][] dp2 = new Double[s+1][capacity.get(0)+1][capacity.get(2)+1];
        HashMap<String, Integer> map = new HashMap<>(); // a map to remember the position of the best solution for the big items
         //initialization
        for (int x1 = 0; x1 < dp[0].length; x1++) {
            for (int x2 = 0; x2 < dp[0][x1].length; x2++) {
                // find the best solution for the big items
                // dp2[0][x1][x2] = Arrays.stream(dp[bigItems.size()][x1][x2]).filter(d -> d != null).min(Double::compare).orElse(null);
                // remember the position of best solution
                for (int i = 0; i < dp[b][x1][x2].length; i++) {
                    if (dp[b][x1][x2][i] != null) {
                        String key = x1 + "," + x2;
                        if (map.containsKey(key)) {
                            if (dp[b][x1][x2][i] < dp2[0][x1][x2]) {
                                map.put(key, i);
                                dp2[0][x1][x2] = dp[b][x1][x2][i];
                            }
                        } else {
                            map.put(key, i);
                            dp2[0][x1][x2] = dp[b][x1][x2][i];
                        }
                    }
                }
            }
        }

        //acutal dp
        // solve the knapsack problem for the remaining small items
        for (int i = 1; i <= s; i++) {
            Integer[] costs = smallItems.get(i-1).getCosts();
            Vector3D[] weights = smallItems.get(i-1).getWeights();
            for (int x1 = 0; x1 < dp2[0].length; x1++) {
                for (int x2 = 0; x2 < dp2[0][x1].length; x2++) {
                    double minVal = Double.MAX_VALUE;
                    for (int c = 0; c < costs.length; c++) { //for the choices
                        Vector3D w = weights[c];
                        int x1_ = x1 - w.get(0);
                        int x2_ = x2 - w.get(2);
                        if (x1_ < 0 || x2_ < 0) {
                            continue;
                        }
                        if (dp2[i-1][x1_][x2_] == null) {
                            continue;
                        }
                        if (dp2[i-1][x1_][x2_] + costs[c] < minVal) {
                            minVal = dp2[i-1][x1_][x2_] + costs[c];
                        }
                    }
                    if (minVal < Double.MAX_VALUE) {
                        dp2[i][x1][x2] = minVal;
                    }
                }
            }
        }


        Vector3D minValue = new Vector3D(0, 0, 0);
        double minCost = Double.MAX_VALUE;
        for (int x1 = 0; x1 < dp2[0].length; x1++) {
            for (int x2 = 0; x2 < dp2[0][x1].length; x2++) {
                if (dp2[s][x1][x2] != null && dp2[s][x1][x2] < minCost) {
                    minCost = dp2[s][x1][x2];
                    minValue = new Vector3D(x1, 0, x2);
                }
            }
        }
        minValue.set(1, map.get(minValue.get(0) + "," + minValue.get(2))); // set the 2nd dimension to the best solution for the big items
        //reconstruction for small items
        for (int i = s; i > 0; i--) {
            MDKnapsackItem item = smallItems.get(i - 1);
            for (KnapsackChoice choice : item.getChoices()) {
                Vector3D newWeight = minValue.subtract(choice.getWeight());
                if (newWeight.get(0) < 0 || newWeight.get(1) < 0 || newWeight.get(2) < 0) {
                    continue;
                }
                if (dp2[i-1][newWeight.get(0)][newWeight.get(2)] != null) {
                    switch (choice.getAllotment()) {
                        case SMALL:
                            smallJobs.add(item.getJob());
                            break;
                        case SEQUENTIAL:
                            seqJobs.add(item.getJob());
                            break;
                        case SHELF1:
                            shelf1.add(item.getJob());
                            break;
                        case SHELF2:
                            shelf2.add(item.getJob());
                            break;
                    }
                    minValue = newWeight;
                    break;  //break out of loop as soon as some allotment was found.
                }
            }
        }

        //reconstruction for big items
        for (int i = b; i > 0; i--) {
            MDKnapsackItem item = bigItems.get(i - 1);
            for (KnapsackChoice choice : item.getChoices()) {
                Vector3D newWeight = minValue.subtract(choice.getWeight());
                if (newWeight.get(0) < 0 || newWeight.get(1) < 0 || newWeight.get(2) < 0) {
                    continue;
                }
                if (dp[i-1][newWeight.get(0)][newWeight.get(2)][newWeight.get(1)] != null) {
                    switch (choice.getAllotment()) {
                        case SMALL:
                            smallJobs.add(item.getJob());
                            break;
                        case SEQUENTIAL:
                            seqJobs.add(item.getJob());
                            break;
                        case SHELF1:
                            shelf1.add(item.getJob());
                            break;
                        case SHELF2:
                            shelf2.add(item.getJob());
                            break;
                    }
                    minValue = newWeight;
                    break;  //break out of loop as soon as some allotment was found.
                }
            }
        }
    }
}
