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
 * This publisher emits a sequence of integers from 1 to the specified count.
 */
public class CustomPublisher implements Publisher<Integer> {

    private final int count;

    public CustomPublisher(int count) {
        this.count = count;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        // Rule 1.9: Publisher.subscribe MUST call onSubscribe on the provided Subscriber
        if (subscriber == null) throw new NullPointerException("Rule 1.9: Subscriber cannot be null");

        // Handshake: Send the Subscription to the Subscriber
        subscriber.onSubscribe(new CustomSubscription(subscriber, count));
    }

    private static class CustomSubscription implements Subscription {
        private final Subscriber<? super Integer> subscriber;
        private final Iterator<Integer> iterator;
        private final AtomicLong demand = new AtomicLong(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private final AtomicInteger wip = new AtomicInteger(0);
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
                // Rule 3.9: If the request is non-positive, MUST signal onError with IllegalArgumentException
                terminated = true;
                subscriber.onError(new IllegalArgumentException("Rule 3.9: n must be positive"));
                return;
            }

            // Rule 3.3: Subscription.request MUST be additive and handle long overflow
            // Using safe addition for demand
            for (; ; ) {
                long currentDemand = demand.get();
                if (currentDemand == Long.MAX_VALUE) {
                    break;
                }
                long nextDemand = currentDemand + n;
                if (nextDemand < 0) {
                    nextDemand = Long.MAX_VALUE;
                }
                if (demand.compareAndSet(currentDemand, nextDemand)) {
                    break;
                }
            }

            drain();
        }

        @Override
        public void cancel() {
            // Rule 3.5: Subscription.cancel() must stop signals
            cancelled.set(true);
        }

        private void drain() {
            // Standard WIP drain loop to ensure thread-safety (Rule 1.2)
            if (wip.getAndIncrement() != 0) {
                return;
            }

            int missed = 1;
            do {
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
                missed = wip.addAndGet(-missed);
            } while (missed != 0);
        }
    }
}
