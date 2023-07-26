package de.ohnes.AlgorithmicComponents.Knapsack;

import java.util.List;

import de.ohnes.util.Job;
import de.ohnes.util.MDKnapsackItem;
import de.ohnes.util.Vector3D;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MDKnapsack {
    
    public void solve(List<MDKnapsackItem> items, Vector3D capacity, List<Job> shelf1, List<Job> shelf2, List<Job> smallJobs, List<Job> seqJobs) {
        DPEntry[][][][] dp = new DPEntry[items.size()+1][capacity.get(0)+1][capacity.get(1)+1][capacity.get(2)+1];


        //initialization
        for (int x1 = 0; x1 <= dp[0].length; x1++) {
            for (int x2 = 0; x2 <= dp[0][x1].length; x2++) {
                for (int x3 = 0; x3 <= dp[0][x1][x2].length; x3++) {
                    dp[0][x1][x2][x3].setDefined(true);
                    dp[0][x1][x2][x3].setValue(0);

                }
            }
        }

        //acutal dp
        for (int i = 1; i < items.size(); i++) {
            Integer[] costs = items.get(i).getCosts();
            Vector3D[] weights = items.get(i).getWeights();
            for (int x1 = 0; x1 <= dp[0].length; x1++) {
                for (int x2 = 0; x2 <= dp[0][x1].length; x2++) {
                    for (int x3 = 0; x3 <= dp[0][x1][x2].length; x3++) {
                        if (!capacity.isSmallerElementWise(x1, x2, x3)) {
                            continue; //skip if weight is too big. //TODO: needed??
                        }
                        double minVal = Double.MAX_VALUE;
                        for (int c = 0; c < costs.length; c++) {
                            Vector3D w = weights[i];
                            int x1_ = x1 - w.get(0);
                            int x2_ = x2 - w.get(1);
                            int x3_ = x3 - w.get(2);
                            if (x1_ < 0 || x2_ < 0 || x3_ < 0) {
                                continue;
                            }
                            if (dp[i][x1_][x2_][x3_].getValue() + costs[i] < minVal) {
                                minVal = dp[i][x1_][x2_][x3_].getValue() + costs[i];
                            }
                        }
                        if (minVal < Double.MAX_VALUE) {
                            dp[i][x1][x2][x3].setDefined(true);
                            dp[i][x1][x2][x3].setValue(minVal);
                        }
                    }
                }
        }
        }


        //reconstruction

    }
}

@Getter
@Setter
@NoArgsConstructor
class DPEntry {
    private boolean defined = false;
    private double value;
}
