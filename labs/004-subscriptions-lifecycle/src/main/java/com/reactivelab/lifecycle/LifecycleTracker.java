package com.reactivelab.lifecycle;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Scenario 5: The Lifecycle Watcher
 * Demonstrates the use of side effect operators to track the lifecycle of a
 * stream.
 */
public class LifecycleTracker {

    /**
     * Attaches lifecycle hooks to a stream and records events into the provided
     * list.
     */
    public Flux<String> trackLifecycle(Flux<String> source, List<String> eventLog) {
        return source
                .doOnSubscribe(sub -> eventLog.add("SUBSCRIBE"))
                .doOnNext(item -> eventLog.add("NEXT:" + item))
                .doOnComplete(() -> eventLog.add("COMPLETE"))
                .doOnError(err -> eventLog.add("ERROR"))
                .doOnCancel(() -> eventLog.add("CANCEL"))
                .doFinally(signalType -> eventLog.add("FINALLY:" + signalType));
    }
}
