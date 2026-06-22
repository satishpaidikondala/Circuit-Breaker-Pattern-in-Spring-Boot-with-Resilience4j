package com.circuitbreaker.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/circuit-breaker")
public class CircuitBreakerInfoController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerInfoController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @GetMapping("/state")
    public String getCircuitBreakerState() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        return circuitBreaker.getState().name();
    }

    @GetMapping("/metrics")
    public Map<String, Object> getCircuitBreakerMetrics() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

        Map<String, Object> result = new HashMap<>();
        result.put("state", circuitBreaker.getState().name());
        result.put("failureRate", metrics.getFailureRate());
        result.put("successRate", 100.0 - metrics.getFailureRate());
        result.put("numberOfCalls", metrics.getNumberOfCalls());
        result.put("numberOfSuccessfulCalls", metrics.getNumberOfSuccessfulCalls());
        result.put("numberOfFailedCalls", metrics.getNumberOfFailedCalls());
        result.put("numberOfNotPermittedCalls", metrics.getNumberOfNotPermittedCalls());
        result.put("slidingWindowSize", metrics.getMaxNumberOfCalls());

        return result;
    }
}
