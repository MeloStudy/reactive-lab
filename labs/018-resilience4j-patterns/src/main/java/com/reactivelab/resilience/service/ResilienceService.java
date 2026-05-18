package com.reactivelab.resilience.service;

import com.reactivelab.resilience.exception.ServiceUnavailableException;
import com.reactivelab.resilience.model.Analytics;
import com.reactivelab.resilience.model.Order;
import com.reactivelab.resilience.model.Report;
import com.reactivelab.resilience.model.Weather;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class ResilienceService {

    public Mono<Order> getOrder(String id, boolean fail) {
        if (fail) {
            return Mono.error(new ServiceUnavailableException("Downstream order database failure"));
        }
        return Mono.just(new Order(id, "COMPLETED", 150.00));
    }

    public Mono<Report> generateReport(String id, long delayMs) {
        return Mono.just(new Report(id, "Financial Report - " + id, "Confidential financial data content"))
                .delayElement(Duration.ofMillis(delayMs));
    }

    public Mono<Weather> getWeather(String city) {
        return Mono.just(new Weather(city, "24°C", "Partly Cloudy"));
    }

    public Mono<Analytics> getAnalytics(String id, long delayMs) {
        return Mono.just(new Analytics(id, 8827L, "OPTIMAL"))
                .delayElement(Duration.ofMillis(delayMs));
    }
}
