package org.evosuite.smartcombine;

import com.sun.org.apache.bcel.internal.generic.IAND;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.mutation.Mutation;
import org.evosuite.coverage.mutation.MutationPool;
import org.evosuite.instrumentation.mutation.ReplaceComparisonOperator;
import org.evosuite.utils.LoggingUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.*;

/**
 * Remove mutants according to this paper: Identifying method-level mutation subsumption relations using Z3
 * https://www.sciencedirect.com/science/article/abs/pii/S095058492030238X
 */
public class SubsumptionRemovingLastingMutantsStrategy implements RemoveLastingMutantsStrategy{



    @Override
    public List<Mutation> removeLastingMutants(List<Mutation> lastingMutants) {
        List<Mutation> removeMutants = new ArrayList<>();
        Map<AbstractInsnNode, List<Mutation>> insnNodeMutationMap = new HashMap<>();

        Map<Mutation, Integer>  miMap =  MutationPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getMutationOpcodesMap();
        for (Mutation m: lastingMutants) {
            if(!insnNodeMutationMap.containsKey(m.getOriginalNode())) {
                insnNodeMutationMap.put(m.getOriginalNode(), new ArrayList<>());
            }
            insnNodeMutationMap.get(m.getOriginalNode()).add(m);

        }
        for(AbstractInsnNode n: insnNodeMutationMap.keySet()) {
            List<Mutation> mutations = insnNodeMutationMap.get(n);
            switch (n.getOpcode()) {
                case Opcodes.IADD: // + -> *
                    removeMutants.addAll(removeIADDMutations(n, miMap, mutations));
                    break;
                case Opcodes.LADD: // + -> *
                    removeMutants.addAll(removeLADDMutations(n, miMap, mutations));
                    break;
                case Opcodes.ISUB: // - -> *
                    removeMutants.addAll(removeISUBMutations(n, miMap, mutations));
                    break;
                case Opcodes.LSUB: // - -> *
                    removeMutants.addAll(removeLSUBMutations(n, miMap, mutations));
                    break;
                case Opcodes.IMUL: // * -> +
                    removeMutants.addAll(removeIMULMutations(n, miMap, mutations));
                    break;
                case Opcodes.LMUL: // * -> +
                    removeMutants.addAll(removeLMULMutations(n, miMap, mutations));
                    break;
                case Opcodes.IDIV: // / -> *, -
                    removeMutants.addAll(removeIDIVMutations(n, miMap, mutations));
                    break;
                case Opcodes.LDIV: // / -> *, -
                    removeMutants.addAll(removeLDIVMutations(n, miMap, mutations));
                    break;
                case Opcodes.IF_ICMPEQ: // == -> >=, <=
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLE)));
                    break;
                case Opcodes.IF_ICMPNE: // != -> >, <
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IF_ICMPGT, Opcodes.IF_ICMPLT)));
                    break;
                case Opcodes.IF_ICMPGT: // !=, >=
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IF_ICMPNE, Opcodes.IF_ICMPGE)));
                    break;
                case Opcodes.IF_ICMPGE:// >= -> =, >
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPGT)));
                    break;
                case Opcodes.IF_ICMPLT: // <= -> <,!=
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IF_ICMPLE, Opcodes.IF_ICMPNE)));
                    break;
                case Opcodes.IF_ICMPLE: // < -> =, <=
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IF_ICMPEQ, Opcodes.IF_ICMPLT)));
                    break;

                case Opcodes.IAND: // & -> |
                case Opcodes.IXOR: // & -> |
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IOR)));
                    break;
                case Opcodes.IOR: // | -> &,^
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IXOR, Opcodes.IAND)));
                    break;

                case Opcodes.LAND: // & -> |
                case Opcodes.LXOR: // & -> |
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LOR)));
                    break;
                case Opcodes.LOR: // | -> &,^
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LXOR, Opcodes.LAND)));
                    break;

                case Opcodes.ISHR: // can not reduce
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.ISHL, Opcodes.IUSHR)));
                    break;
                case Opcodes.ISHL: // << -> >>
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.ISHR)));
                case Opcodes.IUSHR: // can not reduce
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.ISHL, Opcodes.ISHR)));
                    break;

                case Opcodes.LSHR: // can not reduce
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LSHL, Opcodes.LUSHR)));
                    break;
                case Opcodes.LSHL: // << -> >>
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LSHR)));
                case Opcodes.LUSHR: // can not reduce
                    removeMutants.addAll(removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LSHL, Opcodes.LSHR)));
                    break;
                default:
                    break;
            }
        }
        LoggingUtils.getEvoLogger().warn(String.format("remove %d mutants with subsumption strategy by SmartCombine", removeMutants.size()));
        return removeMutants;
    }

    private List<Mutation> removeMutationsBySubsumptionMinimalSet(Map<Mutation, Integer> miMap, List<Mutation> mutations, Set<Integer> minimalSet) {
        List<Mutation> removeMutations = new ArrayList<>();
        for (Mutation mutation : mutations) {
            if (!minimalSet.contains(miMap.get(mutation))) {
                removeMutations.add(mutation);
            }
        }
        return removeMutations;
    }

    protected static Set<Integer>  createMinimalSet(Integer... opcodes) {
        if(Properties.SMART_COMBINE_RANDOM_MUTANT_MINIMAL_SET_SIZE > 0 && Properties.SMART_COMBINE_RANDOM_MUTANT_MINIMAL_SET_SIZE <= opcodes.length) {
            return new HashSet<>(Arrays.asList(opcodes).subList(0, Properties.SMART_COMBINE_RANDOM_MUTANT_MINIMAL_SET_SIZE));
        }
        return new HashSet<>(Arrays.asList(opcodes));
    }

    // Use the subsumption relationship of lexp + rexp (Assuming they are greater  0)
    private List<Mutation> removeLADDMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LMUL));
    }
    private List<Mutation> removeIADDMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IMUL));
    }

    // Use the subsumption relationship of lexp - rexp (Assuming they are greater  0), the result of this paper chooses lexp as the minimal set
    // But Evosuite does not implement this mutant in ReplaceArithmeticOperator, So we use lexp * rexp according to the full subsumption graph provided by this paper.
    private List<Mutation> removeLSUBMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LMUL));
    }
    private List<Mutation> removeISUBMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IMUL));
    }

    // Use the subsumption relationship of lexp * rexp (Assuming they are greater  0), the result of this paper chooses lexp as the minimal set
    // But Evosuite does not implement this mutant in ReplaceArithmeticOperator, So we use lexp + rexp according to the full subsumption graph provided by this paper.
    private List<Mutation> removeLMULMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LADD));
    }
    private List<Mutation> removeIMULMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IADD));
    }

    // Use the subsumption relationship of lexp / rexp (Assuming they are greater  0)
    private List<Mutation> removeLDIVMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.LMUL, Opcodes.LSUB));
    }
    private List<Mutation> removeIDIVMutations(AbstractInsnNode n, Map<Mutation, Integer> miMap, List<Mutation> mutations) {
        return removeMutationsBySubsumptionMinimalSet(miMap, mutations, createMinimalSet(Opcodes.IMUL, Opcodes.ISUB));
    }


}
