package com.reactivelab.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * A raw implementation of the Reactive Streams Publisher interface.
 * <p>
 * PEDAGOGICAL NOTE:
 * This class demonstrates the "inner workings" of a Reactive Stream. In production,
 * you would use Project Reactor (Flux/Mono), but understanding this "low-level"
 * plumbing is essential for mastering backpressure and concurrency.
 */
public class CustomPublisher implements Publisher<Integer> {

    private final int count;

    public CustomPublisher(int count) {
        this.count = count;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        // Rule 1.9: Publisher.subscribe MUST call onSubscribe on the provided Subscriber
        // before any other signals are sent.
        if (subscriber == null) throw new NullPointerException("Rule 1.9: Subscriber cannot be null");

        // Handshake: We create a "contract" (Subscription) and give it to the Subscriber.
        // The Subscriber now has control over when and how much data it receives.
        subscriber.onSubscribe(new CustomSubscription(subscriber, count));
    }

    /**
     * The Subscription is the "engine" of the stream. It manages state and demand.
     */
    private static class CustomSubscription implements Subscription {
        private final Subscriber<? super Integer> subscriber;
        private final Iterator<Integer> iterator;

        // --- CONCURRENCY CONTROL ---
        // demand: Tracks how many items the subscriber has requested but hasn't received yet.
        private final AtomicLong demand = new AtomicLong(0);

        // canceled: Tracks if the subscriber wants to stop receiving data.
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        // wip (Work-In-Progress): A critical "gatekeeper" variable used to ensure that 
        // signals (onNext, onComplete) are sent sequentially (Rule 1.2), even if 
        // request() is called from multiple threads.
        private final AtomicInteger wip = new AtomicInteger(0);

        // terminated: A simple flag to avoid sending signals after an error or completion.
        private volatile boolean terminated = false;

        public CustomSubscription(Subscriber<? super Integer> subscriber, int count) {
            this.subscriber = subscriber;
            this.iterator = IntStream.rangeClosed(1, count).iterator();
        }

        @Override
        public void request(long n) {
            if (terminated || cancelled.get()) {
                return;
            }

            if (n <= 0) {
                // Rule 3.9: If the request is non-positive, MUST signal onError with IllegalArgumentException.
                // This is a terminal state.
                terminated = true;
                subscriber.onError(new IllegalArgumentException("Rule 3.9: n must be positive"));
                return;
            }

            // Rule 3.3: Subscription.request MUST be additive and handle long overflow.
            // We use a CAS (Compare-And-Swap) loop instead of 'synchronized' for performance.
            // This ensures thread-safe addition without blocking the calling thread.
            for (; ; ) {
                long currentDemand = demand.get();
                if (currentDemand == Long.MAX_VALUE) {
                    break; // Already unbounded, no need to add more.
                }
                long nextDemand = currentDemand + n;
                // PEDAGOGICAL NOTE: In Java, adding to Long.MAX_VALUE causes an overflow, 
                // resulting in a negative number. The Reactive Streams spec dictates 
                // that any demand exceeding Long.MAX_VALUE should be capped at Long.MAX_VALUE (unbounded).
                if (nextDemand < 0) {
                    nextDemand = Long.MAX_VALUE;
                }

                if (demand.compareAndSet(currentDemand, nextDemand)) {
                    break;
                }
            }

            // After updating demand, we try to "drain" the requested items to the subscriber.
            drain();
        }

        @Override
        public void cancel() {
            // Rule 3.5: Subscription.cancel() must stop signals and be idempotent.
            cancelled.set(true);
        }

        /**
         * The DRAIN LOOP (WIP Loop) Pattern.
         * This is the "heartbeat" of reactive implementations. It ensures:
         * 1. Thread-safety: Only one thread at a time executes the emission logic.
         * 2. Non-blocking: If another thread is already draining, we just exit.
         * 3. Fairness: It processes "missed" signals that occurred while draining.
         */
        private void drain() {
            // 1. THE GATEKEEPER (Mutual Exclusion):
            // Only the thread that successfully transitions from 0 to 1 enters.
            // Others increment WIP and exit, ensuring onNext is serialized (Rule 1.2).
            if (wip.getAndIncrement() != 0) {
                return;
            }

            int missed = 1;
            do {
                // 2. THE DRAIN (The Emission Loop):
                // We emit as long as there is demand, we haven't been canceled, and haven't terminated.
                while (demand.get() > 0 && !cancelled.get() && !terminated) {
                    if (iterator.hasNext()) {
                        subscriber.onNext(iterator.next());
                        demand.decrementAndGet();
                    } else {
                        terminated = true;
                        subscriber.onComplete();
                        break;
                    }
                }

                // 3. THE CATCH-UP (WIP Check):
                // If wip > 1, someone called request() while we were emitting.
                // We subtract what we processed and, if the result is not 0, we loop again.
                missed = wip.addAndGet(-missed);
            } while (missed != 0);
        }
    }
}
