package com.reactivelab.generation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

/**
 * Scenario 3: The Radio Broadcast (Cold vs Hot)
 * Demonstrates the difference between independent streams (Cold) 
 * and shared live streams (Hot).
 */
@Slf4j
public class Broadcaster {

    /**
     * A cold flux that starts from the beginning for every subscriber.
     * Each subscriber gets their own timeline.
     */
    public Flux<String> getColdStream() {
        return Flux.just("Line 1", "Line 2", "Line 3")
                .delayElements(Duration.ofMillis(100))
                .doOnSubscribe(s -> log.info("Cold Stream: New subscriber joined. Starting from the beginning."));
    }

    /**
     * A hot flux that shares the same execution and misses data for late joiners.
     * '.share()' is a shortcut for 'publish().refCount(1)'.
     */
    public Flux<String> getHotStream(Flux<String> coldSource) {
        log.info("Converting Cold source to Hot stream via .share()");
        return coldSource
                .doOnNext(item -> log.debug("Hot Stream Broadcasting: {}", item))
                .share();
    }

    /**
     * Scenario 6: The Modern Sinks API (Manual Push)
     * Demonstrates a multicast sink with replay capabilities.
     * This allows us to manually push events into a stream from anywhere.
     */
    public Sinks.Many<String> createNotificationSink() {
        log.info("Creating a multicast Sink with replay(1) strategy.");
        // Replay(1) ensures late subscribers get the last notification instantly
        return Sinks.many().replay().limit(1);
    }
}
