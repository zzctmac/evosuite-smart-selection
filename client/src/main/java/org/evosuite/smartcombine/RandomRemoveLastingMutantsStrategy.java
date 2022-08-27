package org.evosuite.smartcombine;

import org.evosuite.Properties;
import org.evosuite.coverage.mutation.Mutation;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;

import java.util.ArrayList;
import java.util.List;

public class RandomRemoveLastingMutantsStrategy implements RemoveLastingMutantsStrategy {
    @Override
    public List<Mutation> removeLastingMutants(List<Mutation> lastingMutants) {
        List<Mutation> removeMutants = new ArrayList<>();
        for(Mutation m : lastingMutants) {
            double c = Randomness.nextDouble();
            if(c <= Properties.SMART_COMBINE_RANDOM_REMOVE_MUTANT_RATE) {
                removeMutants.add(m);
            }
        }
        LoggingUtils.getEvoLogger().warn(String.format("remove %d mutants with random strategy by SmartCombine", removeMutants.size()));
        return removeMutants;
    }
}
