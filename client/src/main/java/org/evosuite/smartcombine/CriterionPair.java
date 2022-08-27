package org.evosuite.smartcombine;

import org.evosuite.Properties;

public class CriterionPair {
    protected Properties.Criterion a;
    protected Properties.Criterion b;

    public CriterionPair(Properties.Criterion a, Properties.Criterion b) {
        this.a = a;
        this.b = b;
    }

    public Properties.Criterion getKey() {
        return a;
    }

    public Properties.Criterion getValue() {
        return b;
    }
}
