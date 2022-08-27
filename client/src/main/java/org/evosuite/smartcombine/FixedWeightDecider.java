package org.evosuite.smartcombine;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.FitnessFunctionWeightDecider;

public class FixedWeightDecider<T extends Chromosome<T>> extends FitnessFunctionWeightDecider<T> {
    private static final long serialVersionUID = 3157648844056198407L;

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double weight = 1;
    @Override
    public double getWeight(FitnessFunction<T> fitnessFunction) {
        if(!this.enabled) {
            return 1;
        }
        return weight;
    }
}
