package de.ohnes.AlgorithmicComponents.Approximation;

import de.ohnes.util.Instance;

public class TrivialLowerBound implements Approximation {

    @Override
    public double approximate(Instance I) {
        
        double totalWork = 0;

        for(int i = 0; i < I.getN(); i++) {
            totalWork += Math.min(I.getJob(i).getSequentialProcessingTime(), I.getJob(i).getProcessingTime(1));
        }

        return totalWork / (I.getM() + I.getL());

    }
    
}
