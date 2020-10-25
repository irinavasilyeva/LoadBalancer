package com.iptiq.assignment;

import com.iptiq.assignment.algorithm.NextStrategy;

import java.util.List;

public interface ProviderStorage {

    /**
     * Adds provider to the storage if it is not present and the storage is not full
     * @param provider instance
     * @return true if added
     */
    boolean addProvider(Provider provider);

    /**
     * Removes provider if present
     * @param provider instance
     * @return true if removed
     */
    boolean removeProvider(Provider provider);

    /**
     * Gets the next provider according to the strategy algorithm
     * @param nextStrategy strategy which defines the algorithm
     * @return Provider instance
     */
    Provider getNext(NextStrategy nextStrategy);

    /**
     * Gets the list of providers (copy of the original list)
     * @return list of providers
     */
    List<Provider> getAllProviders();

    /**
     * Gets number of providers currently in the list
     * @return number of providers
     */
    int numberOfActiveProviders();
}
