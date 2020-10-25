package com.iptiq.assignment.algorithm;

public interface NextStrategy {

    /**
     * Returns next index, if currentAmount is 0, then -1
     * @param currentAmount currentAmount of instances in storage
     * @return next
     */
    int getNext(int currentAmount);
}
