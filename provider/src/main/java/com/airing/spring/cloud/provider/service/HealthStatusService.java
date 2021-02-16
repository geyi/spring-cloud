package com.airing.spring.cloud.provider.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

@Service
public class HealthStatusService implements HealthIndicator {

    private boolean status = true;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public Health health() {
        if (status) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }
    }
}
