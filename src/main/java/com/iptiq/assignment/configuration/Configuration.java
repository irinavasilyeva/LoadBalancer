package com.iptiq.assignment.configuration;

import com.iptiq.assignment.algorithm.Algorithm;
import lombok.Builder;
import lombok.Value;

/**
 * Created only one for simplicity
 * More considerations: there could be different configuration for different purposes (e.g. StorageConfiguration, LoadBalancerConfiguration)
 * Configuration could be created in any way (getting values from file, envvars, app params, etc.)
 */
@Builder
@Value
public class Configuration {
    int maxInstances;
    Algorithm algorithm;
    int numberOfConcurrentRequest;
    int intervalToCheck;
}
