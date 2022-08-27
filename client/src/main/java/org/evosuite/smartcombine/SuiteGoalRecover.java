package org.evosuite.smartcombine;

import org.evosuite.Properties;
import org.evosuite.coverage.line.LineCoverageSuiteFitness;
import org.evosuite.coverage.line.LineCoverageTestFitness;
import org.evosuite.coverage.mutation.Mutation;
import org.evosuite.coverage.mutation.MutationSuiteFitness;
import org.evosuite.coverage.mutation.WeakMutationSuiteFitness;
import org.evosuite.ga.FitnessFunctionWeightDecider;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.SearchListener;
import org.evosuite.rmi.ClientServices;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.evosuite.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.List;

public class SuiteGoalRecover implements SearchListener<TestSuiteChromosome> {


    private static final long serialVersionUID = -3768843836843549684L;
    private static List<LineCoverageTestFitness> keepLineGoals;
    private static List<Mutation> keepMutationGoals;
    private static int lineOriginSize;
    private static int mutationOriginSize;
    private final List<FitnessFunctionWeightDecider<TestSuiteChromosome>> fitnessWeightDeciders = new ArrayList<>();

    protected LineCoverageSuiteFitness lineCoverageSuiteFitness;
    protected MutationSuiteFitness mutationSuiteFitness;
    protected List<TestSuiteFitnessFunction> removeFitnesses;
    protected static LineCoverageSuiteFitness lineCoverageSuiteFitnessFromSelectLine;
    protected static MutationSuiteFitness mutationSuiteFitnessSelectMutation;


    public static void recordLineSelected(List<LineCoverageTestFitness> goals, LineCoverageSuiteFitness lineCoverageSuiteFitness, int originSize) {
        lineCoverageSuiteFitnessFromSelectLine = lineCoverageSuiteFitness;
        keepLineGoals = goals;
        lineOriginSize = originSize;
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SelectedLineNumber, String.format("%d", goals.size()));
    }

    public static void recordMutationSelected(List<Mutation> goals, MutationSuiteFitness mutationSuiteFitness, int originSize) {
        mutationSuiteFitnessSelectMutation = mutationSuiteFitness;
        keepMutationGoals = goals;
        mutationOriginSize = originSize;
        ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.SelectedMutationNumber, String.format("%d", goals.size()));
    }

    public SuiteGoalRecover(LineCoverageSuiteFitness lineCoverageSuiteFitness, WeakMutationSuiteFitness weakMutationSuiteFitness, List<TestSuiteFitnessFunction> removeFitnessFunctions) {
        this.lineCoverageSuiteFitness = lineCoverageSuiteFitness;
        this.mutationSuiteFitness = weakMutationSuiteFitness;
        RelativeWeightDecider<TestSuiteChromosome> lineWeightDecider = new RelativeWeightDecider<>(Properties.SMART_COMBINE_GOAL_WEIGHT, lineOriginSize, keepLineGoals.size());
        this.lineCoverageSuiteFitness.setFitnessFunctionWeightDecider(lineWeightDecider);
        this.fitnessWeightDeciders.add(lineWeightDecider);

        RelativeWeightDecider<TestSuiteChromosome> wmWeightDecider = new RelativeWeightDecider<>(Properties.SMART_COMBINE_GOAL_WEIGHT, mutationOriginSize, keepMutationGoals.size());
        this.mutationSuiteFitness .setFitnessFunctionWeightDecider(wmWeightDecider);
        this.fitnessWeightDeciders.add(wmWeightDecider);
        this.removeFitnesses = removeFitnessFunctions;
    }

    @Override
    public void searchStarted(GeneticAlgorithm<TestSuiteChromosome> algorithm) {

    }

    @Override
    public void iteration(GeneticAlgorithm<TestSuiteChromosome> algorithm) {

    }

    @Override
    public void searchFinished(GeneticAlgorithm<TestSuiteChromosome> algorithm) {
        Properties.SMART_COMBINE = false;
        this.fitnessWeightDeciders.forEach(c -> {c.setEnabled(false);});
        if(this.lineCoverageSuiteFitness != null) {
            assert lineCoverageSuiteFitnessFromSelectLine == lineCoverageSuiteFitness;
            algorithm.removeFitnessFunction(this.lineCoverageSuiteFitness);
            algorithm.addFitnessFunction(new LineCoverageSuiteFitness());
        }
        if(this.mutationSuiteFitness != null) {
            assert mutationSuiteFitnessSelectMutation == mutationSuiteFitness;
            algorithm.removeFitnessFunction(this.mutationSuiteFitness);
            algorithm.addFitnessFunction(new WeakMutationSuiteFitness()); // we add WeakMutation despite origin mutationSuiteFitness may not be WeakMutation. Need improve!
        }
        for(TestSuiteFitnessFunction f : removeFitnesses) {
            algorithm.addFitnessFunction(f);
        }
        algorithm.updateFitnessFunctionsAndValues();
    }

    @Override
    public void fitnessEvaluation(TestSuiteChromosome individual) {

    }

    @Override
    public void modification(TestSuiteChromosome individual) {

    }
}
