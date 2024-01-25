package de.ohnes.AlgorithmicComponents.Knapsack;

import java.util.HashMap;
import java.util.List;

import de.ohnes.AlgorithmicComponents.Shelves.CpuGpuApproach;
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
     * @param capacity the capacity of the knapsack. Note that the third dimension is calculated dynamically by using the rounding terms mu an v TODO.
     * @param shelf1
     * @param shelf2
     * @param smallJobs
     * @param seqJobs
     * @param mu rounding factor used for small jobs
     * @param v rounding factor used for big jobs
     * @return true if a solution exists, false otherwise.
     */
    public boolean solve(List<MDKnapsackItem> smallItems, List<MDKnapsackItem> bigItems, Vector3D capacity, List<Job> shelf1, List<Job> shelf2, List<Job> smallJobs, List<Job> seqJobs, double mu, double v) {

        final int b = bigItems.size();
        final int s = smallItems.size();
        final int n = s + b;
        final double muDivV = mu / v;
        final int invDelta = CpuGpuApproach.invDelta;
        
        // 1st dimension: number of items
        // 2nd dimension: T_1 uses at most M machines (malleable constraint)
        // 3rd dimension: total work on L
        // 4th dimension: total weight on L (not used by small jobs)
        Double[][][][] BigDP = new Double[b + 1][capacity.get(0) + 1][capacity.get(1) + 1][capacity.get(2) + 1];
        
        
        //initialization
        for (int x1 = 0; x1 < BigDP[0].length; x1++) {
            for (int x2 = 0; x2 < BigDP[0][x1].length; x2++) {
                for (int x3 = 0; x3 < BigDP[0][x1][x2].length; x3++) {
                    BigDP[0][x1][x2][x3] = 0.0;
                }
            }
        }
        
        //acutal dp
        // fist solve the knapsack problem for the big items
        for (int i = 1; i <= bigItems.size(); i++) { //for all big jobs
            boolean placeable = false; //variable to keep track if the job is placeable at all.
            Integer[] costs = bigItems.get(i-1).getCosts();
            Vector3D[] weights = bigItems.get(i-1).getWeights();
            for (int x1 = 0; x1 < BigDP[0].length; x1++) { 
                for (int x2 = 0; x2 < BigDP[0][x1].length; x2++) {
                    for (int x3 = 0; x3 < BigDP[0][x1][x2].length; x3++) {
                        double minVal = Double.MAX_VALUE;
                        for (int c = 0; c < costs.length; c++) { //for the choices
                            Vector3D w = weights[c];
                            int x1_ = x1 - w.get(0);
                            //qHat = \floor{p_i/v} - weight(i) * \floor{d/3*v}
                            int x2_ = x2 - (int) Math.floor(w.get(1) * v) - w.get(2) * (int) Math.floor(2 * invDelta / 3); //TODO remove math.floor
                            int x3_ = x3 - w.get(2);
                            if (x1_ < 0 || x2_ < 0 || x3_ < 0) {
                                continue;
                            }
                            if (BigDP[i-1][x1_][x2_][x3_] == null) {
                                continue;
                            }
                            if (BigDP[i-1][x1_][x2_][x3_] + costs[c] < minVal) {
                                minVal = BigDP[i-1][x1_][x2_][x3_] + costs[c];
                            }
                        }
                        if (minVal < Double.MAX_VALUE) {
                            BigDP[i][x1][x2][x3] = minVal;
                            placeable = true;
                        }
                    }
                }
            }
            if (!placeable) {
                return false; //if the job is not placeable, then the knapsack problem is not solvable.
            }
        }

        //discard 2nd constraint, as the small items don't change it.
        
        Double[][][] SmallDP = new Double[s + 1][capacity.get(0)+1][n * invDelta + 1];
        //TODO: this map is not strictly necessary, but it makes the code more readable.
        HashMap<String, int[]> map = new HashMap<>(); // a map to remember the position of the best solution for the big items
         //initialization
        for (int x1 = 0; x1 < BigDP[0].length; x1++) {
            for (int x2 = 0; x2 < BigDP[0][x1].length; x2++) {
                // 2l/\delta - y* \floor{2/3\delta}
                int x3UpperBound = capacity.get(1) * invDelta - x2 * (int) Math.floor(2 * invDelta / 3); //TODO: remove math.floor
                // find the best solution for the big items
                // remember the position of best solution
                for (int x3 = 0; x3 < Math.min(x3UpperBound, BigDP[0][x1][x2].length); x3++) { //TODO: check correctness
                    Double p = BigDP[b][x1][x2][x3];
                    if (p != null) {
                        // \floor{(w\floor{d/3 * v} + qHat) *mu/v}
                        int x2Rescaled = (int) Math.floor((x2 * (int) Math.floor(2 * invDelta / 3) + x3) * muDivV); //TODO: remove math.floor
                        String key = x1 + "," + x2Rescaled;
                        if (map.containsKey(key)) {
                            if (BigDP[b][x1][x2][x3] < SmallDP[0][x1][x2Rescaled]) {
                                map.put(key, new int[]{x2, x3});
                                SmallDP[0][x1][x2Rescaled] = p;
                            }
                        } else {
                            map.put(key, new int[]{x2, x3});
                            SmallDP[0][x1][x2Rescaled] = p; //TODO: this throws an IndexOutOfBoundsException
                        }
                    }
                }
            }
        }

        //acutal dp
        // solve the knapsack problem for the remaining small items
        for (int i = 1; i <= s; i++) {
            boolean placeable = false; //variable to keep track if the job is placeable at all.
            Integer[] costs = smallItems.get(i-1).getCosts();
            Vector3D[] weights = smallItems.get(i-1).getWeights();
            for (int x1 = 0; x1 < SmallDP[0].length; x1++) {
                for (int x2 = 0; x2 < SmallDP[0][x1].length; x2++) {
                    double minVal = Double.MAX_VALUE;
                    for (int c = 0; c < costs.length; c++) { //for the choices
                        Vector3D w = weights[c];
                        int x1_ = x1 - w.get(0);
                        int x2_ = x2 - (int) Math.floor(w.get(2) * mu); //round the processing time accordingly.
                        if (x1_ < 0 || x2_ < 0) {
                            continue;
                        }
                        if (SmallDP[i-1][x1_][x2_] == null) {
                            continue;
                        }
                        if (SmallDP[i-1][x1_][x2_] + costs[c] < minVal) {
                            minVal = SmallDP[i-1][x1_][x2_] + costs[c];
                        }
                    }
                    if (minVal < Double.MAX_VALUE) { //this is to find the minimum value along the 3rd dimension
                        SmallDP[i][x1][x2] = minVal;
                        placeable = true;
                    }
                }
            }
            if (!placeable) {
                return false; //if the job is not placeable, then the knapsack problem is not solvable.
            }
        }

        Vector3D minValue = new Vector3D(0, 0, 0);
        double minCost = Double.MAX_VALUE;
        for (int x1 = 0; x1 < SmallDP[0].length; x1++) {
            for (int x2 = 0; x2 < SmallDP[0][x1].length; x2++) {
                if (SmallDP[s][x1][x2] != null && SmallDP[s][x1][x2] < minCost) {
                    minCost = SmallDP[s][x1][x2];
                    minValue.set(0, x1);
                    minValue.set(2, x2);
                }
            }
        }

        //reconstruction for small items
        for (int i = s - 1; i >= 0; i--) {
            MDKnapsackItem item = smallItems.get(i);
            boolean placed = false;
            for (KnapsackChoice choice : item.getChoices()) {
                // subtract the first dimension and the rounded 3rd dimension from the current weight
                Vector3D newWeight = minValue.subtract(choice.getWeight().get(0), 0, (int) Math.floor(choice.getWeight().get(2) * mu));
                if (newWeight.get(0) < 0 || newWeight.get(1) < 0 || newWeight.get(2) < 0) {
                    continue;
                }
                if (SmallDP[i][newWeight.get(0)][newWeight.get(2)] != null && SmallDP[i][newWeight.get(0)][newWeight.get(2)] + choice.getCost() == SmallDP[i+1][minValue.get(0)][minValue.get(2)]) {
                    assert SmallDP[i][newWeight.get(0)][newWeight.get(2)] + choice.getCost() == SmallDP[i+1][minValue.get(0)][minValue.get(2)];
                    placed = true;
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
            assert placed;
        }

        // reconstruct the solution for the big items

        // recover the 3rd dimension
        int[] bigIndex = map.get(minValue.get(0) + "," + minValue.get(1));
        minValue.set(1, bigIndex[0]);
        minValue.set(2, bigIndex[1]);


        //reconstruction for big items
        for (int i = b; i > 0; i--) {
            boolean placed = false;
            MDKnapsackItem item = bigItems.get(i - 1);
            for (KnapsackChoice choice : item.getChoices()) {
                //subtract the first and third dimension and the rounded 2nd dimension from the current weight
                int x2 = (int) Math.floor(choice.getWeight().get(1) * v) - choice.getWeight().get(2) * 4; 
                Vector3D newWeight = minValue.subtract(choice.getWeight().get(0), x2, choice.getWeight().get(2));
                if (newWeight.get(0) < 0 || newWeight.get(1) < 0 || newWeight.get(2) < 0) {
                    continue;
                }
                if (BigDP[i-1][newWeight.get(0)][newWeight.get(1)][newWeight.get(2)] != null && BigDP[i-1][newWeight.get(0)][newWeight.get(1)][newWeight.get(2)] + choice.getCost() == BigDP[i][minValue.get(0)][minValue.get(1)][minValue.get(2)]) {
                    assert BigDP[i-1][newWeight.get(0)][newWeight.get(1)][newWeight.get(2)] + choice.getCost() == BigDP[i][minValue.get(0)][minValue.get(1)][minValue.get(2)];
                    placed = true;
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
            assert placed;
        }
        // at the end we should arrive at 0.0
        assert BigDP[0][minValue.get(0)][minValue.get(1)][minValue.get(2)] == 0.0;
        return true;
    }
}
