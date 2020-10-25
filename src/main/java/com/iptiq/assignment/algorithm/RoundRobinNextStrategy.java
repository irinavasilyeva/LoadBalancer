package com.iptiq.assignment.algorithm;

/**
 * This is not entirely correct for the current LoadBalancer
 * since Providers could be excluded and there won't be the proper sequence
 * It is as simple as possible
 */
class RoundRobinNextStrategy implements NextStrategy {
    private int last = -1;

    @Override
    public int getNext(int currentAmount) {
        if (currentAmount == 0) {
            last = -1;
        } else if (last >= currentAmount - 1) {
            last = 0;
        } else {
            last++;
        }

        return last;
    }
}
