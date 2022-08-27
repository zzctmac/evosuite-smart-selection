package org.evosuite.smartcombine;

import org.evosuite.coverage.mutation.Mutation;

import java.util.List;

interface RemoveLastingMutantsStrategy {
    List<Mutation> removeLastingMutants(List<Mutation>lastingMutants);
}
