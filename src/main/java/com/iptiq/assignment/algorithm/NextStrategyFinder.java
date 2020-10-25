package com.iptiq.assignment.algorithm;

public class NextStrategyFinder {

    /**
     * It could be extended with multiple other algorithms
     * @param algorithm defines which strategy to use
     * @return NextStrategy
     */
    public static NextStrategy findNextStrategy(Algorithm algorithm) {
        return algorithm == Algorithm.ROUND_ROBIN ? new RoundRobinNextStrategy()
                : new RandomNextStrategy();
    }
}
