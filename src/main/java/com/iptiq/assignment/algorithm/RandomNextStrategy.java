package com.iptiq.assignment.algorithm;

import java.util.concurrent.ThreadLocalRandom;

class RandomNextStrategy implements NextStrategy {
    @Override
    public int getNext(int currentAmount) {
        return currentAmount == 0 ? -1
            : ThreadLocalRandom.current().nextInt(0, currentAmount);
    }
}
