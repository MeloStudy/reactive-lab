package com.reactivelab.resilience.controller;

import com.reactivelab.resilience.model.Analytics;
import com.reactivelab.resilience.model.Order;
import com.reactivelab.resilience.service.ResilienceService;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/resilient")
public class ResilienceController {

    private final ResilienceService resilienceService;
    private final CircuitBreaker orderCircuitBreaker;
    private final Bulkhead reportBulkhead;
    private final RateLimiter weatherRateLimiter;
    private final TimeLimiter analyticsTimeLimiter;

    public ResilienceController(ResilienceService resilienceService,
                                CircuitBreaker orderCircuitBreaker,
                                Bulkhead reportBulkhead,
                                RateLimiter weatherRateLimiter,
                                TimeLimiter analyticsTimeLimiter) {
        this.resilienceService = resilienceService;
        this.orderCircuitBreaker = orderCircuitBreaker;
        this.reportBulkhead = reportBulkhead;
        this.weatherRateLimiter = weatherRateLimiter;
        this.analyticsTimeLimiter = analyticsTimeLimiter;
    }

    @GetMapping("/orders/{id}")
    public Mono<Order> getOrder(@PathVariable String id, @RequestParam(defaultValue = "false") boolean fail) {
        return resilienceService.getOrder(id, fail)
                .transformDeferred(CircuitBreakerOperator.of(orderCircuitBreaker))
                .onErrorResume(ex -> Mono.just(Order.builder()
                        .id(id)
                        .status("FALLBACK_ORDER")
                        .amount(0.0)
                        .build()));
    }

    @GetMapping("/report")
    public Mono<ResponseEntity<Object>> getReport(
            @RequestParam(defaultValue = "1") String id,
            @RequestParam(defaultValue = "100") long delayMs) {
        return resilienceService.generateReport(id, delayMs)
                .transformDeferred(BulkheadOperator.of(reportBulkhead))
                .map(report -> ResponseEntity.ok((Object) report))
                .onErrorResume(BulkheadFullException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                .body(new ErrorResponse("Bulkhead Limit Reached", "Max 2 concurrent report generations permitted", 429)))
                );
    }

    @GetMapping("/weather")
    public Mono<ResponseEntity<Object>> getWeather(@RequestParam(defaultValue = "London") String city) {
        return resilienceService.getWeather(city)
                .transformDeferred(RateLimiterOperator.of(weatherRateLimiter))
                .map(weather -> ResponseEntity.ok((Object) weather))
                .onErrorResume(RequestNotPermitted.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                .header("Retry-After", "5")
                                .body(new ErrorResponse("Too Many Requests", "Rate limit exceeded. Max 3 requests per 5 seconds", 429)))
                );
    }

    @GetMapping("/analytics")
    public Mono<Analytics> getAnalytics(
            @RequestParam(defaultValue = "main") String id,
            @RequestParam(defaultValue = "0") long delayMs) {
        return resilienceService.getAnalytics(id, delayMs)
                .transformDeferred(TimeLimiterOperator.of(analyticsTimeLimiter))
                .onErrorResume(ex -> Mono.just(Analytics.builder()
                        .id(id)
                        .requestCount(0L)
                        .status("CACHED_FALLBACK")
                        .build()));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String message;
        private int status;
    }
}
