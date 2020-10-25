package com.iptiq.assignment;

import com.iptiq.assignment.algorithm.NextStrategy;
import com.iptiq.assignment.algorithm.NextStrategyFinder;
import com.iptiq.assignment.configuration.Configuration;
import com.iptiq.assignment.monitoring.HealthChecker;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Which exception strategy we should choose, should we use codes?
 * Proper logging should be added
 */
public class LoadBalancer {
    private final Configuration configuration;
    private final ProviderStorage providerStorage;
    private final NextStrategy nextStrategy;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    //IMPORTANT: when to decrement this value?
    private int currentNumberOfRequests;

    public LoadBalancer(Configuration configuration, ProviderStorage providerStorage) {
        this.configuration = configuration;
        this.providerStorage = providerStorage;
        this.nextStrategy = NextStrategyFinder.findNextStrategy(configuration.getAlgorithm());
    }

    public void establishHealthChecker() {
        new HealthChecker().check(providerStorage,
                configuration.getIntervalToCheck(),
                this::exclude,
                this::include);
    }

    /**
     * throws IllegalStateException if no providers registered, if capacity limits exceeded
     *
     * @return id of the chosen provider
     */
    public String get() {
        if (!capacityLimitExceeded()) {
            lock.readLock().lock();
            String next;
            try {
                next = providerStorage.getNext(nextStrategy)
                        .get();
            } finally {
                lock.readLock().unlock();
            }

            return recheckAndReturn(next);
        }

        throw new IllegalStateException("Capacity limit exceeded");
    }

    /**
     * Tries to register a list of providers
     *
     * @param providerList to register
     * @return true if all registered
     */
    public boolean register(List<Provider> providerList) {
        lock.writeLock().lock();
        try {
            long countOfAdded = providerList.stream()
                    .map(providerStorage::addProvider)
                    .filter(added -> added)
                    .count();

            return providerList.size() == countOfAdded;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Tries to register a new provider
     *
     * @param provider to register
     * @return true if registered
     */
    public boolean include(Provider provider) {
        lock.writeLock().lock();
        try {
            return providerStorage.addProvider(provider);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Tries to remove a provider
     *
     * @param provider to remove
     * @return true if removed
     */
    public boolean exclude(Provider provider) {
        lock.writeLock().lock();
        try {
            boolean excluded = providerStorage.removeProvider(provider);
            if (excluded) decrementCurrentNumberOfRequest();

            return excluded;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void decrementCurrentNumberOfRequest() {
        currentNumberOfRequests = currentNumberOfRequests - configuration.getNumberOfConcurrentRequest();
    }

    private boolean capacityLimitExceeded() {
        return currentNumberOfRequests >=
                configuration.getNumberOfConcurrentRequest() * providerStorage.numberOfActiveProviders();
    }

    private String recheckAndReturn(String next) {
        lock.writeLock().lock();
        try {
            if (!capacityLimitExceeded()) {
                currentNumberOfRequests++;
                return next;
            }

        } finally {
            lock.writeLock().unlock();
        }

        throw new IllegalStateException("Capacity limit exceeded");
    }
}
