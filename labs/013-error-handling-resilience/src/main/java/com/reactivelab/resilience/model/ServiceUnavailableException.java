package com.reactivelab.resilience.model;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException() {
        super("Downstream service is currently unavailable");
    }
}
