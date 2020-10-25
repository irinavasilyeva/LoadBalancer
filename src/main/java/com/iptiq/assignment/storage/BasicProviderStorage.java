package com.iptiq.assignment.storage;

import com.iptiq.assignment.algorithm.NextStrategy;
import com.iptiq.assignment.Provider;
import com.iptiq.assignment.ProviderStorage;
import com.iptiq.assignment.configuration.Configuration;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This component should be thread safe
 */
@RequiredArgsConstructor
public class BasicProviderStorage implements ProviderStorage {
    private final Configuration configuration;
    private final List<Provider> providers = new ArrayList<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean addProvider(Provider provider) {
        lock.writeLock().lock();
        try {
            if (configuration.getMaxInstances() > providers.size()
                && !providers.contains(provider)) {
                providers.add(provider);
                return true;
            }

            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeProvider(Provider provider) {
        lock.writeLock().lock();
        try {
            if (providers.contains(provider)) {
                providers.remove(provider);
                return true;
            }

            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Provider getNext(NextStrategy nextStrategy) {
        lock.readLock().lock();
        try {
            if (providers.size() == 0)
                throw new IllegalStateException("No providers registered");

            int next = nextStrategy.getNext(providers.size());
            return providers.get(next);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Provider> getAllProviders() {
        lock.readLock().lock();
        try {
            return List.copyOf(providers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int numberOfActiveProviders() {
        lock.readLock().lock();
        try {
            return providers.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
