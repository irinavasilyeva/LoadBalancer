package com.iptiq.assignment;

import com.iptiq.assignment.algorithm.Algorithm;
import com.iptiq.assignment.configuration.Configuration;
import com.iptiq.assignment.provider.BasicProvider;
import com.iptiq.assignment.storage.BasicProviderStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Integration test
 * Just to show that all the requirements were met
 */
public class LoadBalancerTest {

    private LoadBalancer loadBalancer;
    private ProviderStorage providerStorage;

    @BeforeEach
    public void init() {
        createLoadBalancer(Algorithm.RANDOM, 3);
    }

    @Test
    public void shouldRegisterProvider() {
        Provider provider = new BasicProvider();
        loadBalancer.register(List.of(provider));

        assertEquals(1, providerStorage.getAllProviders().size());
        assertEquals(provider, providerStorage.getAllProviders().get(0));
    }

    @Test
    public void shouldNotRegisterMoreThan10() {
        List<Provider> providers = IntStream.rangeClosed(0, 12)
                .mapToObj(i -> new BasicProvider())
                .collect(Collectors.toList());

        assertFalse(loadBalancer.register(providers));
        assertEquals(10, providerStorage.getAllProviders().size());
    }

    @Test
    public void shouldReturnProviderInstanceId() {
        Provider provider = new BasicProvider();
        loadBalancer.register(List.of(provider));

        assertEquals(provider.get(), loadBalancer.get());
    }

    @Test
    public void shouldUseRoundRobinWhenDefined() {
        createLoadBalancer(Algorithm.ROUND_ROBIN, 3);

        Provider provider1 = new BasicProvider();
        Provider provider2 = new BasicProvider();
        Provider provider3 = new BasicProvider();
        loadBalancer.register(List.of(provider1, provider2, provider3));

        assertEquals(provider1.get(), loadBalancer.get());
        assertEquals(provider2.get(), loadBalancer.get());
        assertEquals(provider3.get(), loadBalancer.get());
        assertEquals(provider1.get(), loadBalancer.get());
    }

    @Test
    public void shouldIncludeProvider() {
        Provider provider = new BasicProvider();
        loadBalancer.include(provider);

        assertEquals(1, providerStorage.getAllProviders().size());
        assertEquals(provider, providerStorage.getAllProviders().get(0));
    }

    @Test
    public void shouldExcludeProvider() {
        Provider provider1 = new BasicProvider();
        Provider provider2 = new BasicProvider();
        loadBalancer.register(List.of(provider1, provider2));

        assertEquals(2, providerStorage.getAllProviders().size());

        loadBalancer.exclude(provider1);
        assertEquals(1, providerStorage.getAllProviders().size());
        assertEquals(provider2, providerStorage.getAllProviders().get(0));
    }

    @Test
    public void shouldNotProvideWhenCapacityLimitExceeded() {
        createLoadBalancer(Algorithm.RANDOM, 2);
        Provider provider = new BasicProvider();
        loadBalancer.register(List.of(provider));

        assertEquals(provider.get(), loadBalancer.get());
        assertEquals(provider.get(), loadBalancer.get());
        assertThrows(IllegalStateException.class, () -> loadBalancer.get());
    }

    @Test
    public void shouldSetUpHealthChecker() {
        Provider provider = Mockito.mock(Provider.class);
        when(provider.check()).thenReturn(false);
        loadBalancer.register(List.of(provider));

        assertEquals(1, providerStorage.getAllProviders().size());

        loadBalancer.establishHealthChecker();
        waitFor(1010);

        assertEquals(0, providerStorage.getAllProviders().size());
    }

    @Test
    public void shouldSetUpHealthCheckerWithProviderAddingWhenAliveAgain() {
        Provider provider = Mockito.mock(Provider.class);
        when(provider.check())
                .thenReturn(false)
                .thenReturn(true)
                .thenReturn(true);
        loadBalancer.register(List.of(provider));

        assertEquals(1, providerStorage.getAllProviders().size());

        loadBalancer.establishHealthChecker();

        waitFor(1010);
        assertEquals(0, providerStorage.getAllProviders().size());

        waitFor(3000);
        assertEquals(1, providerStorage.getAllProviders().size());
    }

    private void createLoadBalancer(Algorithm algorithm, int numberOfRequest) {
        Configuration configuration = Configuration.builder()
                .algorithm(algorithm)
                .intervalToCheck(1)
                .numberOfConcurrentRequest(numberOfRequest)
                .maxInstances(10)
                .build();

        providerStorage = new BasicProviderStorage(configuration);
        loadBalancer = new LoadBalancer(configuration, providerStorage);
    }

    private void waitFor(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
