package com.reactivelab.spec;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
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
        if (subscriber == null) throw new NullPointerException("Subscriber cannot be null");
        
        // Handshake: Send the Subscription to the Subscriber
        subscriber.onSubscribe(new CustomSubscription(subscriber, count));
    }

    private static class CustomSubscription implements Subscription {
        private final Subscriber<? super Integer> subscriber;
        private final Iterator<Integer> iterator;
        private final AtomicLong demand = new AtomicLong(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private boolean terminated = false;

        public CustomSubscription(Subscriber<? super Integer> subscriber, int count) {
            this.subscriber = subscriber;
            this.iterator = IntStream.rangeClosed(1, count).iterator();
        }

        @Override
        public void request(long n) {
            if (n <= 0) {
                // Rule 3.9: non-positive request must signal onError
                subscriber.onError(new IllegalArgumentException("Rule 3.9: n must be positive"));
                return;
            }

            // Rule 3.3: request(n) must be additive
            long previousDemand = demand.getAndAdd(n);
            
            // If we were already draining, let that process handle the new demand.
            // If previous demand was 0, we start the drain loop.
            if (previousDemand == 0) {
                drain();
            }
        }

        @Override
        public void cancel() {
            // Rule 3.5: Subscription.cancel() must stop signals
            cancelled.set(true);
        }

        private void drain() {
            // Simple drain loop to handle demand
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
        }
    }
}
