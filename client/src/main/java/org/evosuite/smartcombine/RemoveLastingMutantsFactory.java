package org.evosuite.smartcombine;

import org.evosuite.Properties;
import org.evosuite.coverage.mutation.Mutation;

import java.util.List;

import static org.evosuite.Properties.SmartCombineRemoveMutantStrategy.SUBSUMPTION;


public class RemoveLastingMutantsFactory {

    public static List<Mutation> removeLastingMutants(List<Mutation> lastingMutants) {
        RemoveLastingMutantsStrategy s;
        switch (Properties.SMART_COMBINE_REMOVE_MUTANT_STRATEGY) {
            case SUBSUMPTION:
                s = new SubsumptionRemovingLastingMutantsStrategy();
                break;
            default:
                s = new RandomRemoveLastingMutantsStrategy();
        }
        return s.removeLastingMutants(lastingMutants);
    }
}
