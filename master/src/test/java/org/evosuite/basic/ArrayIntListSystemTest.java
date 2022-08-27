package org.evosuite.basic;

import com.examples.with.different.packagename.ArrayIntList;
import com.examples.with.different.packagename.NullInteger;
import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.strategy.TestGenerationStrategy;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

public class ArrayIntListSystemTest  extends SystemTestBase {
    @Test
    public void testSuite() {
        EvoSuite evosuite = new EvoSuite();

        String targetClass = ArrayIntList.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;

        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.CBRANCH, Properties.Criterion.LINE, Properties.Criterion.BRANCH, Properties.Criterion.WEAKMUTATION};
        Properties.SMART_COMBINE_REMOVE_MUTANT_STRATEGY = Properties.SmartCombineRemoveMutantStrategy.SUBSUMPTION;


        Object result = evosuite.parseCommandLine(command);
        GeneticAlgorithm<TestSuiteChromosome> ga = getGAFromResult(result);
        TestSuiteChromosome best = ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
        Assert.assertEquals("Wrong number of goals: ", 6, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }
}
