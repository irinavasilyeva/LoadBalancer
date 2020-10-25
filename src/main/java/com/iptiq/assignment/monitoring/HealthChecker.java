package com.iptiq.assignment.monitoring;

import com.iptiq.assignment.Provider;
import com.iptiq.assignment.ProviderStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HealthChecker {

    private final Map<Provider, Boolean> deadProviders = new HashMap<>();

    public void check(ProviderStorage storage,
                      int interval,
                      Consumer<Provider> actionIfFailed,
                      Consumer<Provider> actionIfSucceed) {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(checkTask(storage, actionIfFailed, actionIfSucceed), 0, interval, TimeUnit.SECONDS);
    }

    private Runnable checkTask(ProviderStorage storage,
                               Consumer<Provider> actionIfFailed,
                               Consumer<Provider> actionIfSucceed) {
        return () -> {
            try {
                List<Provider> newlyFoundDeadProviders = storage.getAllProviders().stream()
                        .filter(p -> !p.check())
                        .collect(Collectors.toList());

                for (Provider provider : deadProviders.keySet()) {
                    boolean alive = provider.check();
                    if (alive && deadProviders.get(provider)) {
                        actionIfSucceed.accept(provider);
                    } else {
                        deadProviders.put(provider, alive);
                    }
                }

                for (Provider provider : newlyFoundDeadProviders) {
                    deadProviders.put(provider, false);
                    actionIfFailed.accept(provider);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };
    }
}
