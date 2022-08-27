package org.evosuite.smartcombine;

import org.evosuite.Properties;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.coverage.line.LineCoverageFactory;
import org.evosuite.coverage.line.LineCoverageSuiteFitness;
import org.evosuite.coverage.line.LineCoverageTestFitness;
import org.evosuite.coverage.mutation.*;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.TestSuiteFitnessFunctionMock;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.rmi.ClientServices;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.evosuite.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.evosuite.strategy.TestGenerationStrategy.getFitnessFactories;

public class Utils {
    public static boolean enableSmartCombine() {
        return Properties.SMART_COMBINE;
    }
    public static int getLineSpan() {
        return Properties.SMART_COMBINE_LINE_SPAN;
    }

    public static SuiteGoalRecover getSuiteGoalRecover(GeneticAlgorithm<TestSuiteChromosome> algorithm, List<TestSuiteFitnessFunction> fitnessFunctions) {
        LineCoverageSuiteFitness lf = null;
        WeakMutationSuiteFitness mf = null;
        List<TestSuiteFitnessFunction> removeFitnessFunctions = new ArrayList<>();
        for (int i = 0; i < fitnessFunctions.size(); i++)  {
            TestSuiteFitnessFunction f = fitnessFunctions.get(i);
            if(f instanceof LineCoverageSuiteFitness) {
                lf = (LineCoverageSuiteFitness) f;
                continue;
            }
            if(f instanceof WeakMutationSuiteFitness) {
                mf = (WeakMutationSuiteFitness) f;
                continue;
            }
            if(MainObjectiveCriteriaSelector.penaltyObjectives.contains(Properties.CRITERION[i])) {
                removeFitnessFunctions.add(f);
                algorithm.removeFitnessFunction(f);
                LoggingUtils.getEvoLogger().warn(String.format("remove %s FitnessFunction by SmartCombine",Properties.CRITERION[i].toString()));
            }
        }
        return new SuiteGoalRecover(lf, mf, removeFitnessFunctions);
    }

    public static MoGoalRecover getMoGoalRecover(GeneticAlgorithm<TestSuiteChromosome> algorithm, List<FitnessFunction<TestSuiteChromosome>> fitnessFunctions) {
        List<FitnessFunction<TestSuiteChromosome>> removeFitnessFunctions = new ArrayList<>();
        Properties.Criterion[] origin;
        origin = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
        Properties.CRITERION = MainObjectiveCriteriaSelector.mainObjectives.toArray(new Properties.Criterion[0]);
        // add mainObjectives
        List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
        for (TestFitnessFactory<? extends TestFitnessFunction> f : goalFactories) {
            for (TestFitnessFunction goal : f.getCoverageGoals()) {
                FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
                fitnessFunctions.add(mock);
            }
        }
        // handle line
        if(MainObjectiveCriteriaSelector.penaltyObjectives.contains(Properties.Criterion.LINE)) {
            Map<Integer, List<LineCoverageTestFitness>> lineGoalMap = (new LineCoverageFactory()).getCoverageGoalsWithSmartCombineForMOSA();
            for (TestFitnessFunction goal : lineGoalMap.get(0)) {
                FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
                fitnessFunctions.add(mock);
            }
            ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SelectedLineNumber, String.format("%d", lineGoalMap.get(0).size()));

            for (TestFitnessFunction goal : lineGoalMap.get(1)) {
                FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
                removeFitnessFunctions.add(mock);
            }
            MainObjectiveCriteriaSelector.penaltyObjectives.remove(Properties.Criterion.LINE);
        }

        // handle weak mutation
        if(MainObjectiveCriteriaSelector.penaltyObjectives.contains(Properties.Criterion.WEAKMUTATION)) {
            Map<Integer, List<MutationTestFitness>> weakGoalMap = (new MutationFactory(false)).getCoverageGoalsForSmartCombineMOSA();
            for (TestFitnessFunction goal : weakGoalMap.get(0)) {
                FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
                fitnessFunctions.add(mock);
            }
            ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SelectedMutationNumber, String.format("%d", weakGoalMap.get(0).size()));

            for (TestFitnessFunction goal : weakGoalMap.get(1)) {
                FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
                removeFitnessFunctions.add(mock);
            }
            MainObjectiveCriteriaSelector.penaltyObjectives.remove(Properties.Criterion.WEAKMUTATION);
        }
        Properties.CRITERION = MainObjectiveCriteriaSelector.penaltyObjectives.toArray(new Properties.Criterion[0]);
        for(Properties.Criterion c : MainObjectiveCriteriaSelector.penaltyObjectives) {
            LoggingUtils.getEvoLogger().warn(String.format("remove %s FitnessFunction by SmartCombine", c.toString()));

        }
        // otherObjectives
        List<TestFitnessFactory<? extends TestFitnessFunction>> otherGoalFactories = getFitnessFactories();
        for (TestFitnessFactory<? extends TestFitnessFunction> f : otherGoalFactories) {
            for (TestFitnessFunction goal : f.getCoverageGoals()) {
                FitnessFunction<TestSuiteChromosome> mock = new TestSuiteFitnessFunctionMock(goal);
                removeFitnessFunctions.add(mock);
            }
        }

        Properties.CRITERION = origin;
        MoGoalRecover mgr = new MoGoalRecover(removeFitnessFunctions);
        return mgr;
    }
}
