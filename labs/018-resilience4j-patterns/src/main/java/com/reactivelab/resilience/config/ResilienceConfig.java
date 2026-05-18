package com.reactivelab.resilience.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(50.0f)
                .waitDurationInOpenState(Duration.ofSeconds(2))
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .permittedNumberOfCallsInHalfOpenState(2)
                .build();
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    public CircuitBreaker orderCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("orderCircuitBreaker");
    }

    @Bean
    public BulkheadRegistry bulkheadRegistry() {
        BulkheadConfig bulkheadConfig = BulkheadConfig.custom()
                .maxConcurrentCalls(2)
                .maxWaitDuration(Duration.ZERO) // Fail fast when bulkhead limit is exceeded
                .build();
        return BulkheadRegistry.of(bulkheadConfig);
    }

    @Bean
    public Bulkhead reportBulkhead(BulkheadRegistry registry) {
        return registry.bulkhead("reportBulkhead");
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(3)
                .limitRefreshPeriod(Duration.ofSeconds(5))
                .timeoutDuration(Duration.ZERO) // Reject requests immediately if rate limit exceeded
                .build();
        return RateLimiterRegistry.of(rateLimiterConfig);
    }

    @Bean
    public RateLimiter weatherRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("weatherRateLimiter");
    }

    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(500))
                .cancelRunningFuture(true)
                .build();
        return TimeLimiterRegistry.of(timeLimiterConfig);
    }

    @Bean
    public TimeLimiter analyticsTimeLimiter(TimeLimiterRegistry registry) {
        return registry.timeLimiter("analyticsTimeLimiter");
    }
}
