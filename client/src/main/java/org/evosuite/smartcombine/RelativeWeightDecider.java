package org.evosuite.smartcombine;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.FitnessFunctionWeightDecider;

public class RelativeWeightDecider <T extends Chromosome<T>> extends FitnessFunctionWeightDecider<T> {
    private static final long serialVersionUID = 3157648844056198407L;
    private double weight;
    private int total;
    private int current;

    public double getRelative() {
        return res;
    }

    private double res;

    public RelativeWeightDecider(double weight, int total, int current) {
        this.weight = weight;
        this.total = total;
        this.current = current;
        res = (double)total / (double)current * weight;
    }

    @Override
    public double getWeight(FitnessFunction<T> fitnessFunction) {
        if(!this.enabled) {
            return 1;
        }
        return this.res;
    }
}
