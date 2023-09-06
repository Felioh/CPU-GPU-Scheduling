package de.ohnes.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KnapsackChoice {
    private MDKnapsackChoice allotment;
    private int cost;
    private Vector3D weight;
}
