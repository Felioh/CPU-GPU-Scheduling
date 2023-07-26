package de.ohnes.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MDKnapsackItem {

    /**the original job for reconstruction */
    private Job job;

    private List<Choice> choices;

    public void addChoice(MDKnapsackChoice allotment, int cost, Vector3D weight) {
        this.choices.add(new Choice(allotment, cost, weight));
    }
    
    public Integer[] getCosts() {
        return choices.stream().map(Choice::getCost).toArray(Integer[] :: new);
    }
    
    public Vector3D[] getWeights() {
        return choices.stream().map(Choice::getWeight).toArray(Vector3D[] :: new);
    }
}

@Getter
@Setter
@AllArgsConstructor
class Choice {
    private MDKnapsackChoice allotment;
    private int cost;
    private Vector3D weight;
}
