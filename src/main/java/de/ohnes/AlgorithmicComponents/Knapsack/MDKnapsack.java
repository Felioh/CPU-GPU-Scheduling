package de.ohnes.AlgorithmicComponents.Knapsack;

import java.util.List;

import de.ohnes.util.Job;
import de.ohnes.util.KnapsackChoice;
import de.ohnes.util.MDKnapsackItem;
import de.ohnes.util.Vector3D;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MDKnapsack {

    public void solve(List<MDKnapsackItem> items, Vector3D capacity, List<Job> shelf1, List<Job> shelf2, List<Job> smallJobs, List<Job> seqJobs) {
        DPEntry[][][][] dp = new DPEntry[items.size()+1][capacity.get(0)+1][capacity.get(1)+1][capacity.get(2)+1];


        //initialization
        for (int x1 = 0; x1 < dp[0].length; x1++) {
            for (int x2 = 0; x2 < dp[0][x1].length; x2++) {
                for (int x3 = 0; x3 < dp[0][x1][x2].length; x3++) {
                    dp[0][x1][x2][x3] = new DPEntry();
                    dp[0][x1][x2][x3].setDefined(true);
                    dp[0][x1][x2][x3].setValue(0);

                }
            }
        }

        //acutal dp
        for (int i = 1; i <= items.size(); i++) {
            Integer[] costs = items.get(i-1).getCosts();
            Vector3D[] weights = items.get(i-1).getWeights();
            for (int x1 = 1; x1 < dp[0].length; x1++) {
                for (int x2 = 1; x2 < dp[0][x1].length; x2++) {
                    for (int x3 = 1; x3 < dp[0][x1][x2].length; x3++) {
                        double minVal = Double.MAX_VALUE;
                        for (int c = 0; c < costs.length; c++) { //for the choices
                            Vector3D w = weights[c];
                            int x1_ = x1 - w.get(0);
                            int x2_ = x2 - w.get(1);
                            int x3_ = x3 - w.get(2);
                            if (x1_ < 0 || x2_ < 0 || x3_ < 0) {
                                continue;
                            }
                            if (dp[i][x1_][x2_][x3_] == null) { //init.
                                dp[i][x1_][x2_][x3_] = new DPEntry();
                            }
                            if (dp[i][x1_][x2_][x3_].getValue() + costs[c] < minVal) {
                                minVal = dp[i][x1_][x2_][x3_].getValue() + costs[c];
                            }
                        }
                        if (minVal < Double.MAX_VALUE) {
                            dp[i][x1][x2][x3] = new DPEntry();
                            dp[i][x1][x2][x3].setDefined(true);
                            dp[i][x1][x2][x3].setValue(minVal);
                        }
                    }
                }
            }
        }

        Vector3D minValue = new Vector3D(0, 0, 0);
        double minCost = Double.MAX_VALUE;
        for (int x1 = 1; x1 < dp[0].length; x1++) {
            for (int x2 = 1; x2 < dp[0][x1].length; x2++) {
                for (int x3 = 1; x3 < dp[0][x1][x2].length; x3++) { 
                    if (dp[items.size()][x1][x2][x3] != null && dp[items.size()][x1][x2][x3].getValue() < minCost) {
                        minCost = dp[items.size()][x1][x2][x3].getValue();
                        minValue = new Vector3D(x1, x2, x3);
                    }
                }
            }
        }

        //reconstruction
        for (int i = items.size(); i > 0; i--) {
            MDKnapsackItem item = items.get(i - 1);
            for (KnapsackChoice choice : item.getChoices()) {
                Vector3D newWeight = minValue.subtract(choice.getWeight());
                if (newWeight.get(0) < 0 || newWeight.get(1) < 0 || newWeight.get(2) < 0) {
                    continue;
                }
                DPEntry entry = dp[i-1][newWeight.get(0)][newWeight.get(1)][newWeight.get(2)];
                if (entry != null && entry.isDefined()) {
                    switch (choice.getAllotment()) {
                        case SMALL:
                            smallJobs.add(item.getJob());
                            break;
                        case SEQUENCIAL:
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
                    break;  //break out of loop as soon as allotment was found.
                }
            }
        }
    }
}

@Getter
@Setter
@NoArgsConstructor
class DPEntry {
    private boolean defined = false;
    private double value;
}
