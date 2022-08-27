package org.evosuite.smartcombine;

import org.evosuite.coverage.mutation.Mutation;
import org.evosuite.instrumentation.mutation.InsertUnaryOperator;
import org.evosuite.instrumentation.mutation.ReplaceConstant;
import org.evosuite.utils.LoggingUtils;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class Infect0RemovingMutantsStrategy implements RemoveLastingMutantsStrategy{

    protected static Set<Integer> Infect0Opcodes = new HashSet<>();
    {
        Infect0Opcodes.add(Opcodes.IFNULL);
        Infect0Opcodes.add(Opcodes.IFNONNULL);
        Infect0Opcodes.add(Opcodes.IF_ACMPEQ);
        Infect0Opcodes.add(Opcodes.IF_ACMPNE);
        Infect0Opcodes.add(Opcodes.POP);
        Infect0Opcodes.add(Opcodes.POP2);
        Infect0Opcodes.add(Opcodes.LDC);
        Infect0Opcodes.add(Opcodes.IFNE);
    }

    @Override
    public List<Mutation> removeLastingMutants(List<Mutation> lastingMutants) {
        List<Mutation> infect0Mutations = new ArrayList<>();
        String[] infect0Operators = {InsertUnaryOperator.NAME, ReplaceConstant.NAME};
        for (Mutation m : lastingMutants) {
            boolean remove = false;
            for (String op : infect0Operators) {
                if (m.getMutationName().startsWith(op)) {
                    infect0Mutations.add(m);
                    remove = true;
                    break;
                }
            }
            if(remove) {
                continue;
            }
            if(Infect0Opcodes.contains(m.getOriginalNode().getOpcode())) {
                infect0Mutations.add(m);
            }
        }
        return infect0Mutations;
    }
}
