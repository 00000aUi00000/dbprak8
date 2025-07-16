package com.backend.service;

import org.springframework.stereotype.Service;

@Service
public class ConnectionStatusService {
    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
