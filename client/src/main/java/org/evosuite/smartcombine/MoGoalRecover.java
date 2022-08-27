package org.evosuite.smartcombine;

import org.evosuite.Properties;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.SearchListener;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;

import java.util.ArrayList;
import java.util.List;

public class MoGoalRecover  implements SearchListener<TestSuiteChromosome> {
    private static final long serialVersionUID = 160485299018253052L;
    List<FitnessFunction<TestSuiteChromosome>> removeFitnessFunctions;

    public MoGoalRecover(List<FitnessFunction<TestSuiteChromosome>> removeFitnessFunctions) {
        this.removeFitnessFunctions = removeFitnessFunctions;
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
        for (FitnessFunction<TestSuiteChromosome> f : removeFitnessFunctions) {
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
