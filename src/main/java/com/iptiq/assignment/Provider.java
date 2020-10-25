package com.iptiq.assignment;

public interface Provider {

    /**
     * Gets providers instance id
     * @return provider instance id
     */
    String get();

    /**
     * Performs checking if providers is alive
     * @return true if alive
     */
    boolean check();
}
