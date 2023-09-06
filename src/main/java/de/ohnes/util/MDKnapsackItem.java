package de.ohnes.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MDKnapsackItem {

    /**the original job for reconstruction */
    private Job job;

    private List<KnapsackChoice> choices = new ArrayList<>();

    public void addChoice(MDKnapsackChoice allotment, int cost, Vector3D weight) {
        this.choices.add(new KnapsackChoice(allotment, cost, weight));
    }
    
    public Integer[] getCosts() {
        return choices.stream().map(KnapsackChoice::getCost).toArray(Integer[] :: new);
    }
    
    public Vector3D[] getWeights() {
        return choices.stream().map(KnapsackChoice::getWeight).toArray(Vector3D[] :: new);
    }
}
