package com.iptiq.assignment.provider;

import com.iptiq.assignment.Provider;

import java.util.UUID;

public class BasicProvider implements Provider {

    private final String instanceId;

    public BasicProvider() {
        instanceId = UUID.randomUUID().toString();
    }

    @Override
    public String get() {
        return instanceId;
    }

    @Override
    public boolean check() {
        return true;
    }
}
