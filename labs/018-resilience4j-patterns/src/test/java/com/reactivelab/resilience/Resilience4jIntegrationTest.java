package com.reactivelab.resilience;

import com.reactivelab.resilience.controller.ResilienceController;
import com.reactivelab.resilience.model.Analytics;
import com.reactivelab.resilience.model.Order;
import com.reactivelab.resilience.model.Report;
import com.reactivelab.resilience.model.Weather;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.blockhound.BlockHound;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Resilience4jIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CircuitBreaker orderCircuitBreaker;

    @Autowired
    private Bulkhead reportBulkhead;

    @Autowired
    private RateLimiter weatherRateLimiter;

    @Autowired
    private TimeLimiter analyticsTimeLimiter;

    @BeforeAll
    static void initAll() {
        // T015: Integrate BlockHound to intercept blocking calls inside reactive threads.
        // This guarantees that the non-blocking event-loop runtime invariants are strictly preserved.
        BlockHound.builder()
                .allowBlockingCallsInside("io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine", "transitionToClosedState")
                .allowBlockingCallsInside("io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine", "transitionToOpenState")
                .allowBlockingCallsInside("io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine", "transitionToHalfOpenState")
                .allowBlockingCallsInside("io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine", "onError")
                .allowBlockingCallsInside("io.github.resilience4j.circuitbreaker.internal.CircuitBreakerStateMachine", "onSuccess")
                .install();
    }

    @BeforeEach
    void resetState() {
        // Ensure every integration test starts with standard, isolated configurations
        orderCircuitBreaker.transitionToClosedState();
        
        // Reset Bulkhead permits
        // (Resilience4j Bulkheads don't need manual reset as they are release-based, but resetting registries guarantees clean scopes)
    }

    @Test
    void scenario1_CircuitBreakerStateTransitions() throws InterruptedException {
        // GIVEN: The Circuit Breaker starts in a CLOSED state
        assertThat(orderCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        // WHEN: Executing a successful request
        // THEN: Verify the call completes with a 200 OK and status COMPLETED
        webTestClient.get()
                .uri("/api/resilient/orders/order-100?fail=false")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(order -> {
                    assertThat(order.getId()).isEqualTo("order-100");
                    assertThat(order.getStatus()).isEqualTo("COMPLETED");
                    assertThat(order.getAmount()).isEqualTo(150.0);
                });

        // WHEN: Firing 10 failure-inducing calls to exceed our count-based sliding window (10 calls)
        // AND: The failure rate threshold is 50%, meaning at least 5 failures will trip the breaker
        for (int i = 0; i < 10; i++) {
            webTestClient.get()
                    .uri("/api/resilient/orders/order-100?fail=true")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Order.class)
                    .value(order -> {
                        // The controller intercepts the failure and resolves to the safe fallback order
                        assertThat(order.getStatus()).isEqualTo("FALLBACK_ORDER");
                        assertThat(order.getAmount()).isEqualTo(0.0);
                    });
        }

        // THEN: The failure rate (10/10 = 100%) exceeds 50%, forcing the circuit breaker to trip OPEN
        assertThat(orderCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);

        // WHEN: Making a call with fail=false while the breaker is OPEN
        // THEN: The breaker must immediately reject the call (CallNotPermittedException) without invoking the upstream service, yielding the fallback order
        webTestClient.get()
                .uri("/api/resilient/orders/order-100?fail=false")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(order -> {
                    assertThat(order.getStatus()).isEqualTo("FALLBACK_ORDER");
                });

        // WHEN: Waiting for the configured transition delay duration (2 seconds)
        Thread.sleep(2100);

        // THEN: The breaker transitions to HALF_OPEN automatically upon the next request attempt
        // We trigger a call to let the state machine evaluate and transition
        webTestClient.get()
                .uri("/api/resilient/orders/order-100?fail=false")
                .exchange()
                .expectStatus().isOk();

        assertThat(orderCircuitBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
    }

    @Test
    void scenario2_ConcurrentBulkheadRejection() {
        // GIVEN: A Semaphore-based Bulkhead configured to allow maximum 2 concurrent report generations
        // WHEN: Triggering 3 concurrent async report requests in parallel
        List<Mono<HttpStatusCode>> concurrentCalls = List.of(
                Mono.fromCallable(() -> {
                    var response = webTestClient.get()
                            .uri("/api/resilient/report?id=report-1&delayMs=300")
                            .exchange();
                    return response.returnResult(Object.class).getStatus();
                }).subscribeOn(Schedulers.boundedElastic()),
                Mono.fromCallable(() -> {
                    var response = webTestClient.get()
                            .uri("/api/resilient/report?id=report-2&delayMs=300")
                            .exchange();
                    return response.returnResult(Object.class).getStatus();
                }).subscribeOn(Schedulers.boundedElastic()),
                Mono.fromCallable(() -> {
                    var response = webTestClient.get()
                            .uri("/api/resilient/report?id=report-3&delayMs=300")
                            .exchange();
                    return response.returnResult(Object.class).getStatus();
                }).subscribeOn(Schedulers.boundedElastic())
        );

        // THEN: Exactly 2 requests must succeed (HTTP 200 OK), while 1 must be rejected immediately (HTTP 429 Too Many Requests)
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        Flux.merge(concurrentCalls)
                .doOnNext(status -> {
                    if (status == HttpStatus.OK) {
                        successCount.incrementAndGet();
                    } else if (status == HttpStatus.TOO_MANY_REQUESTS) {
                        rejectedCount.incrementAndGet();
                    }
                })
                .then()
                .block(Duration.ofSeconds(5));

        // Validate Semaphore capacity boundaries
        assertThat(successCount.get()).isEqualTo(2);
        assertThat(rejectedCount.get()).isEqualTo(1);
    }

    @Test
    void scenario3_RateLimiterQuotas() {
        // GIVEN: A Rate Limiter allowing 3 requests per 5 seconds
        // WHEN: Rapidly firing 4 requests in less than 1 second
        // THEN: The first 3 succeed, and the 4th is blocked with HTTP 429 and Retry-After: 5
        for (int i = 0; i < 3; i++) {
            webTestClient.get()
                    .uri("/api/resilient/weather?city=Rome")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Weather.class)
                    .value(weather -> {
                        assertThat(weather.getCity()).isEqualTo("Rome");
                        assertThat(weather.getCondition()).isEqualTo("Partly Cloudy");
                    });
        }

        // The 4th request must breach the rate limit quota
        webTestClient.get()
                .uri("/api/resilient/weather?city=Rome")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                .expectHeader().valueEquals("Retry-After", "5")
                .expectBody(ResilienceController.ErrorResponse.class)
                .value(err -> {
                    assertThat(err.getError()).isEqualTo("Too Many Requests");
                    assertThat(err.getMessage()).contains("Rate limit exceeded");
                    assertThat(err.getStatus()).isEqualTo(429);
                });
    }

    @Test
    void scenario4_TimeLimiterTimeouts() {
        // GIVEN: A Time Limiter timeout threshold of 500ms
        // WHEN: Executing a request within the threshold (100ms delay)
        // THEN: The call returns successfully
        webTestClient.get()
                .uri("/api/resilient/analytics?id=analytics-100&delayMs=100")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Analytics.class)
                .value(analytics -> {
                    assertThat(analytics.getId()).isEqualTo("analytics-100");
                    assertThat(analytics.getStatus()).isEqualTo("OPTIMAL");
                    assertThat(analytics.getRequestCount()).isEqualTo(8827L);
                });

        // WHEN: Executing a request exceeding the threshold (800ms delay)
        // THEN: The TimeLimiter throws TimeoutException, and is intercepted to return the cached static fallback representation
        long startTime = System.currentTimeMillis();
        webTestClient.get()
                .uri("/api/resilient/analytics?id=analytics-100&delayMs=800")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Analytics.class)
                .value(analytics -> {
                    assertThat(analytics.getId()).isEqualTo("analytics-100");
                    assertThat(analytics.getStatus()).isEqualTo("CACHED_FALLBACK");
                    assertThat(analytics.getRequestCount()).isEqualTo(0L);
                });
        long duration = System.currentTimeMillis() - startTime;

        // Verify that the request was cut short well before the 800ms delayed publisher completed
        assertThat(duration).isLessThan(700);
    }
}
