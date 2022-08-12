package de.ohnes;

import de.ohnes.AlgorithmicComponents.Algorithm;
import de.ohnes.AlgorithmicComponents.Approximation.Approximation;
import de.ohnes.util.Instance;

public class DualApproximationFramework {
    
    //an fptas that is to be used for a large number of machines (>= 8*(n/epsilon))
    private Algorithm fptas;
    private Algorithm knapsack;
    private Instance I;
    private Approximation approx;

    public DualApproximationFramework(Algorithm fptas, Algorithm knapsack, Approximation approx, Instance I) {
        this.fptas = fptas;
        this.knapsack = knapsack;
        this.approx = approx;
        this.I = I;
    }
    

    public double start(double epsilon) {
        
        
        Algorithm usedAlgo;
        if(I.getM() >= 8 * (I.getN() / epsilon)) {
            usedAlgo = this.fptas;
            usedAlgo.setInstance(I);
        } else {
            usedAlgo = this.knapsack;
            usedAlgo.setInstance(I);
        }
        double lowerBound = this.approx.approximate(I) / 2; //TODO think about exact bounds ( * 4)
        double upperBound = lowerBound * 8;                 //TODO maybe add list scheduling. -> schedule twiari greedy and divide by 2.

        return binarySearch(usedAlgo, epsilon, lowerBound, upperBound);
    }

    private double binarySearch(Algorithm algo, double epsilon, double l, double r) {

        double mid = l + (r - l) / 2;
        I.resetInstance(); //reset the instance because it was altered in previous attempt.
        if(algo.solve(mid, epsilon)) { //a schedule of length "mid" exists

            if(r - mid < epsilon) {     //TODO think about minimal steplength
                return mid;
            }

            return binarySearch(algo, epsilon, l, mid); //try to find a better schedule
        } else {    //no schedule for length "mid" exists
            return binarySearch(algo, epsilon, mid, r); //find a schedule for worse makespan
        }
    }
    
}