package org.evosuite.smartcombine;
import org.evosuite.Properties;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class TestMainObjectiveSelector {
    @Test
    public void testWSACorrelation() {
        List<List<Properties.Criterion>> ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.BRANCH, Properties.Criterion.LINE,
                        Properties.Criterion.EXCEPTION, Properties.Criterion.WEAKMUTATION,
                        Properties.Criterion.OUTPUT, Properties.Criterion.METHOD,
                        Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.CBRANCH  },
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(4, ret.get(0).size());
        assertEquals(4, ret.get(1).size());

    }

    @Test
    public void testWSABranchLine() {
        List<List<Properties.Criterion>> ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.BRANCH, Properties.Criterion.LINE},
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(1, ret.get(0).size());
        assertEquals(Properties.Criterion.BRANCH, ret.get(0).get(0));
        assertEquals(1, ret.get(1).size());
        assertEquals(Properties.Criterion.LINE, ret.get(1).get(0));

        ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.LINE, Properties.Criterion.BRANCH},
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(1, ret.get(0).size());
        assertEquals(Properties.Criterion.BRANCH, ret.get(0).get(0));
        assertEquals(1, ret.get(1).size());
        assertEquals(Properties.Criterion.LINE, ret.get(1).get(0));


    }

    @Test
    public void testWSAMethod() {
        List<List<Properties.Criterion>> ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.METHOD, Properties.Criterion.METHODNOEXCEPTION},
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(1, ret.get(0).size());
        assertEquals(Properties.Criterion.METHODNOEXCEPTION, ret.get(0).get(0));
        assertEquals(1, ret.get(1).size());
        assertEquals(Properties.Criterion.METHOD, ret.get(1).get(0));

        ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.METHOD},
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(1, ret.get(0).size());
        assertEquals(Properties.Criterion.METHODNOEXCEPTION, ret.get(0).get(0));
        assertEquals(1, ret.get(1).size());
        assertEquals(Properties.Criterion.METHOD, ret.get(1).get(0));


    }

    @Test
    public void testWSAOther() {
        List<List<Properties.Criterion>> ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.METHOD, Properties.Criterion.INPUT, Properties.Criterion.ONLYBRANCH},
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(3, ret.get(0).size());
        assertEquals(0, ret.get(1).size());

    }

    @Test
    public void testMOCorrelation() {
        List<List<Properties.Criterion>> ret = MainObjectiveCriteriaSelector.getTwoLayerCriteria(new Properties.Criterion[]{
                        Properties.Criterion.BRANCH, Properties.Criterion.LINE,
                        Properties.Criterion.EXCEPTION, Properties.Criterion.WEAKMUTATION,
                        Properties.Criterion.OUTPUT, Properties.Criterion.METHOD,
                        Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.CBRANCH  },
                MainObjectiveCriteriaSelector.CorrelationList);

        assertEquals(2, ret.size());
        assertEquals(4, ret.get(0).size());
        assertEquals(Properties.Criterion.CBRANCH, ret.get(0).get(0));
        assertEquals(Properties.Criterion.METHODNOEXCEPTION, ret.get(0).get(1));
        assertEquals(Properties.Criterion.EXCEPTION, ret.get(0).get(2));
        assertEquals(Properties.Criterion.OUTPUT, ret.get(0).get(3));

        assertEquals(4, ret.get(1).size());

    }

    @Test
    public void testInitWSA() {
        Properties.STRATEGY = Properties.Strategy.EVOSUITE;
        Properties.SMART_COMBINE = true;
        Properties.CRITERION = new Properties.Criterion[] {
                Properties.Criterion.BRANCH, Properties.Criterion.LINE, Properties.Criterion.EXCEPTION, Properties.Criterion.WEAKMUTATION, Properties.Criterion.OUTPUT, Properties.Criterion.METHOD, Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.CBRANCH  };
        MainObjectiveCriteriaSelector.init();
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.BRANCH));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.METHODNOEXCEPTION));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.EXCEPTION));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.OUTPUT));

        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.WEAKMUTATION));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.LINE));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.CBRANCH));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.METHOD));

    }

    @Test
    public void testInitMO() {
        Properties.STRATEGY = Properties.Strategy.MOSUITE;
        Properties.ALGORITHM = Properties.Algorithm.MOSA;
        Properties.SMART_COMBINE = true;
        Properties.CRITERION = new Properties.Criterion[] {
                Properties.Criterion.BRANCH, Properties.Criterion.LINE, Properties.Criterion.EXCEPTION, Properties.Criterion.WEAKMUTATION, Properties.Criterion.OUTPUT, Properties.Criterion.METHOD, Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.CBRANCH  };
        MainObjectiveCriteriaSelector.init();
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.CBRANCH));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.METHODNOEXCEPTION));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.EXCEPTION));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.OUTPUT));

        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.WEAKMUTATION));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.LINE));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.BRANCH));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.METHOD));

    }

    @Test
    public void testInitDYMO() {
        Properties.STRATEGY = Properties.Strategy.MOSUITE;
        Properties.ALGORITHM = Properties.Algorithm.DYNAMOSA;
        Properties.SMART_COMBINE = true;
        Properties.CRITERION = new Properties.Criterion[] {
                Properties.Criterion.BRANCH, Properties.Criterion.LINE, Properties.Criterion.EXCEPTION, Properties.Criterion.WEAKMUTATION, Properties.Criterion.OUTPUT, Properties.Criterion.METHOD, Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.CBRANCH  };
        MainObjectiveCriteriaSelector.init();
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.CBRANCH));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.METHODNOEXCEPTION));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.EXCEPTION));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.OUTPUT));
        assertEquals(0, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.BRANCH));


        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.WEAKMUTATION));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.LINE));
        assertEquals(1, (int) MainObjectiveCriteriaSelector.orderMap.get(Properties.Criterion.METHOD));

    }


    @Before
    public void prepare() {


    }
}
