package com.reactivelab.streams;

import org.reactivestreams.Publisher;
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

    @Override
    public Publisher<Integer> createFailedPublisher() {
        // Rule 1.9: A failing publisher MUST signal onError
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        // Immediate failure
                        s.onError(new RuntimeException("Rule 1.9: Failed publisher"));
                    }

                    @Override
                    public void cancel() {
                        // No-op
                    }
                });
            }
        };
    }

    @Override
    public long maxElementsFromPublisher() {
        // Limit for testing purposes, but Integer.MAX_VALUE is fine for our range
        return Integer.MAX_VALUE;
    }
}
