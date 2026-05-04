package com.reactivelab.streams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

/**
 * Official Reactive Streams TCK validation for CustomPublisher.
 * This class extends the TCK harness to run a battery of compliance tests.
 */
public class PublisherTCKTest extends PublisherVerification<Integer> {

    public PublisherTCKTest() {
        super(new TestEnvironment(500)); // Timeout in ms
    }

    @Override
    public Publisher<Integer> createPublisher(long elements) {
        // The TCK calls this with various counts to test boundaries
        return new CustomPublisher((int) Math.min(elements, Integer.MAX_VALUE));
    }

    /**
     * PEDAGOGICAL NOTE:
     * Why are we implementing a failed publisher here instead of using CustomPublisher?
     * <p>
     * CustomPublisher is designed to emit valid sequences. The TCK requires a dedicated
     * failing publisher to verify how subscribers handle error signals after a handshake.
     * Implementing it in-place keeps our main logic clean of "test-only" error states.
     */
    @Override
    public Publisher<Integer> createFailedPublisher() {
        // Rule 1.9: A failing publisher MUST signal onError
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                if (s == null) throw new NullPointerException("Rule 1.9: Subscriber cannot be null");
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        // No-op for failed publisher
                    }

                    @Override
                    public void cancel() {
                        // No-op
                    }
                });
                // Immediate failure after handshake
                s.onError(new RuntimeException("Rule 1.9: Failed publisher"));
            }
        };
    }

    @Override
    public long maxElementsFromPublisher() {
        // Limit for testing purposes, but Integer.MAX_VALUE is fine for our range
        return Integer.MAX_VALUE;
    }
}
