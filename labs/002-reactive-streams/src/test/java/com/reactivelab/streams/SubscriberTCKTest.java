package com.reactivelab.streams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;

/**
 * Official Reactive Streams TCK validation for CustomSubscriber.
 * This ensures our test subscriber follows all rules for handling signals,
 * cancellation, and terminal states.
 */
public class SubscriberTCKTest extends SubscriberBlackboxVerification<Integer> {

    public SubscriberTCKTest() {
        super(new TestEnvironment(500));
    }

    @Override
    public Subscriber<Integer> createSubscriber() {
        // We provide a subscriber that requests elements automatically for the TCK tests
        return new CustomSubscriber<>(Long.MAX_VALUE);
    }

    @Override
    public Integer createElement(int element) {
        return element;
    }
}
