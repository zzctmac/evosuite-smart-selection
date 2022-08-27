package org.evosuite.smartcombine;

import org.evosuite.Properties;

import java.util.*;
public class MainObjectiveCriteriaSelector {
    public static Map<Properties.Criterion, Integer> orderMap = new HashMap<>();
    public static List<Properties.Criterion> mainObjectives = new ArrayList<>();
    public static List<Properties.Criterion> penaltyObjectives = new ArrayList<>();


    protected static CriterionPair[]  CorrelationList = new CriterionPair[]{
            new CriterionPair(Properties.Criterion.BRANCH, Properties.Criterion.LINE),
            new CriterionPair(Properties.Criterion.BRANCH, Properties.Criterion.CBRANCH),
            new CriterionPair(Properties.Criterion.BRANCH, Properties.Criterion.WEAKMUTATION),

            new CriterionPair(Properties.Criterion.CBRANCH, Properties.Criterion.BRANCH),
            new CriterionPair(Properties.Criterion.CBRANCH, Properties.Criterion.LINE),
            new CriterionPair(Properties.Criterion.CBRANCH, Properties.Criterion.WEAKMUTATION),

            new CriterionPair(Properties.Criterion.LINE, Properties.Criterion.BRANCH),
            new CriterionPair(Properties.Criterion.LINE, Properties.Criterion.CBRANCH),
            new CriterionPair(Properties.Criterion.LINE, Properties.Criterion.WEAKMUTATION),

            new CriterionPair(Properties.Criterion.WEAKMUTATION, Properties.Criterion.BRANCH),
            new CriterionPair(Properties.Criterion.WEAKMUTATION, Properties.Criterion.CBRANCH),
            new CriterionPair(Properties.Criterion.WEAKMUTATION, Properties.Criterion.LINE),

            new CriterionPair(Properties.Criterion.METHODNOEXCEPTION, Properties.Criterion.METHOD),


    };

    protected static Map<Properties.Criterion, Integer> rankMap = new HashMap<>();

    protected static void initRank() {
        rankMap.put(Properties.Criterion.CBRANCH, 0);
        rankMap.put(Properties.Criterion.METHODNOEXCEPTION, 0);
        rankMap.put(Properties.Criterion.BRANCH, 1);
        rankMap.put(Properties.Criterion.LINE, 2);
    }

    static {
        initRank();
        init();
    }



    protected static void init() {
        if(!Properties.SMART_COMBINE) {
            return;
        }
        List<List<Properties.Criterion>> twoLayerCriteria  = getTwoLayerCriteria(Properties.CRITERION, CorrelationList);
        // we need record branch's location for DynaMOSA
        int branch_in_layer = -1;
        for(int i = 0; i < twoLayerCriteria.size(); i++) {
            for(Properties.Criterion c : twoLayerCriteria.get(i)) {
                orderMap.put(c, i);
                if(c == Properties.Criterion.BRANCH) {
                    branch_in_layer = i;
                }
            }
        }
        // when we use DYNAMOSA and branch is in the layer 2, we need force to put branch into layer 1.
        if(Properties.STRATEGY == Properties.Strategy.MOSUITE && Properties.ALGORITHM == Properties.Algorithm.DYNAMOSA && branch_in_layer == 1) {
            orderMap.put(Properties.Criterion.BRANCH, 0);
        }
        for(Properties.Criterion c : orderMap.keySet()) {
            int o = orderMap.get(c);
            if(o == 0) {
                mainObjectives.add(c);
            } else {
                penaltyObjectives.add(c);
            }
        }
    }



    public static List<List<Properties.Criterion>> getTwoLayerCriteria(Properties.Criterion[] origin, CriterionPair[]correlationList) {
        // init
        List<List<Properties.Criterion>> ret = new ArrayList<>();
        ret.add( new ArrayList<>());
        ret.add(new ArrayList<>());
        List<Properties.Criterion> originList = new ArrayList<>(Arrays.asList(origin));
        // when Coverage of Criterion a and Criterion b have strong correlation(corr > 0.8), we say that a dominates b.
        final Map<Properties.Criterion, Integer> dominateCountMap = new HashMap<>();
        final Map<Properties.Criterion, Integer> indexMap = new HashMap<>();
        for(int i = 0; i < origin.length; i++) {
            indexMap.put(origin[i], i);
            dominateCountMap.put(origin[i], 0);
        }
        for (CriterionPair criterionCriterionPair : correlationList) {
            if (!indexMap.containsKey(criterionCriterionPair.getKey())) {
                continue;
            }
            dominateCountMap.put(criterionCriterionPair.getKey(), dominateCountMap.get(criterionCriterionPair.getKey()) + 1);
        }
        originList.sort((o1, o2) -> {
            if(dominateCountMap.get(o1) > dominateCountMap.get(o2)) {
                return -1;
            }
            if(dominateCountMap.get(o1) < dominateCountMap.get(o2)) {
                return 1;
            }
            final int defaultRank = 10000;
            Integer o1Rank = rankMap.getOrDefault(o1, defaultRank);
            Integer o2Rank = rankMap.getOrDefault(o2, defaultRank);
            if(o1Rank < o2Rank) {
                return -1;
            }
            if(o1Rank > o2Rank) {
                return 1;
            }
            return indexMap.get(o1) < indexMap.get(o2) ? -1 : 1;
        });

        Map<Properties.Criterion, Boolean>setMap = new HashMap<>();
        for(Properties.Criterion c : originList) {
            if(setMap.getOrDefault(c, false)) {
                continue;
            }
            setMap.put(c, true);
            ret.get(0).add(c);
            for(CriterionPair criterionCriterionPair : correlationList) {
                if(criterionCriterionPair.getKey() == c && indexMap.containsKey(criterionCriterionPair.getValue())) {
                    ret.get(1).add(criterionCriterionPair.getValue());
                    setMap.put(criterionCriterionPair.getValue(), true);
                }
            }
        }

        return ret;
    }
}
