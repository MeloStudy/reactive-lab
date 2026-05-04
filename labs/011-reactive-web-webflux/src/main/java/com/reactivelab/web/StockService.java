package com.reactivelab.web;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StockService {

    private final Map<String, Double> prices = new ConcurrentHashMap<>();

    public StockService() {
        prices.put("AAPL", 150.0);
        prices.put("GOOGL", 2800.0);
        prices.put("MSFT", 300.0);
    }

    public Mono<StockQuote> getQuote(String symbol) {
        return Mono.justOrEmpty(prices.get(symbol))
                .map(price -> new StockQuote(symbol, price, Instant.now()))
                .delayElement(Duration.ofMillis(50)) // Simulate network latency
                .switchIfEmpty(Mono.error(new StockNotFoundException(symbol)));
    }

    public Flux<StockQuote> getPriceStream(String symbol) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> {
                    double price = prices.getOrDefault(symbol, 100.0) + (Math.random() * 5 - 2.5);
                    prices.put(symbol, price);
                    return new StockQuote(symbol, price, Instant.now());
                })
                .share(); // Multicast to multiple subscribers
    }

    public Flux<StockQuote> getAllQuotes(int count) {
        return Flux.range(1, count)
                .delayElements(Duration.ofMillis(10))
                .map(i -> new StockQuote("STK-" + i, 100.0 + i, Instant.now()));
    }
}
