/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.basic;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.strategy.TestGenerationStrategy;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.Assert;
import org.junit.Test;

import com.examples.with.different.packagename.NullInteger;

public class NullIntegerSystemTest extends SystemTestBase {

    @Test
    public void testNullInteger() {
        EvoSuite evosuite = new EvoSuite();

        String targetClass = NullInteger.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SMART_COMBINE = true;

        String[] command = new String[]{"-generateSuite", "-class", targetClass};

        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.CBRANCH, Properties.Criterion.LINE, Properties.Criterion.BRANCH, Properties.Criterion.WEAKMUTATION};
        Properties.SMART_COMBINE_REMOVE_MUTANT_STRATEGY = Properties.SmartCombineRemoveMutantStrategy.SUBSUMPTION;


        Object result = evosuite.parseCommandLine(command);
        GeneticAlgorithm<TestSuiteChromosome> ga = getGAFromResult(result);
        TestSuiteChromosome best = ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
        Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverage(), 0.001);
    }

    @Test
    public void testNullIntegerMOSA() {
        EvoSuite evosuite = new EvoSuite();

        String targetClass = NullInteger.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SMART_COMBINE = true;
        Properties.ALGORITHM = Properties.Algorithm.MOSA;
        String[] command = new String[]{"-generateMOSuite",  "-class", targetClass};

        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.CBRANCH, Properties.Criterion.LINE, Properties.Criterion.BRANCH, Properties.Criterion.WEAKMUTATION};


        Object result = evosuite.parseCommandLine(command);
        GeneticAlgorithm<TestSuiteChromosome> ga = getGAFromResult(result);
        TestSuiteChromosome best = ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
    }

    @Test
    public void testNullIntegerDynaMOSA() {
        EvoSuite evosuite = new EvoSuite();

        String targetClass = NullInteger.class.getCanonicalName();

        Properties.TARGET_CLASS = targetClass;
        Properties.SMART_COMBINE = true;
        Properties.ALGORITHM = Properties.Algorithm.DYNAMOSA;
        String[] command = new String[]{"-generateMOSuite",  "-class", targetClass};

        Properties.CRITERION = new Properties.Criterion[]{Properties.Criterion.CBRANCH, Properties.Criterion.LINE, Properties.Criterion.BRANCH, Properties.Criterion.WEAKMUTATION};


        Object result = evosuite.parseCommandLine(command);
        GeneticAlgorithm<TestSuiteChromosome> ga = getGAFromResult(result);
        TestSuiteChromosome best = ga.getBestIndividual();
        System.out.println("EvolvedTestSuite:\n" + best);

        int goals = TestGenerationStrategy.getFitnessFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
        Assert.assertEquals("Wrong number of goals: ", 3, goals);
    }
}
