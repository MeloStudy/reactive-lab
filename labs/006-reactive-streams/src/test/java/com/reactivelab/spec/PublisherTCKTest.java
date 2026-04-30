package com.reactivelab.spec;

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
        // Optional: Implement if we want to test error handling compliance
        return null; 
    }

    @Override
    public long maxElementsFromPublisher() {
        // Limit for testing purposes, but Integer.MAX_VALUE is fine for our range
        return Integer.MAX_VALUE;
    }
}
