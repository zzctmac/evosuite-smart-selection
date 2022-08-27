package org.evosuite.ga;

import java.io.Serializable;

abstract public class FitnessFunctionWeightDecider<T extends Chromosome<T>> implements Serializable {
    private static final long serialVersionUID = 2738552493558142644L;

    abstract public double getWeight(FitnessFunction<T> fitnessFunction);

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    protected boolean enabled = true;
}
