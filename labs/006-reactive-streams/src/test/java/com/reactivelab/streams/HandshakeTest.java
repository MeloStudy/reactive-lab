package com.reactivelab.streams;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HandshakeTest {

    @Test
    @DisplayName("Scenario 1: No data should be emitted without request")
    void shouldNotEmitWithoutRequest() {
        CustomPublisher publisher = new CustomPublisher(10);
        CustomSubscriber<Integer> subscriber = new CustomSubscriber<>();

        publisher.subscribe(subscriber);

        // Even though we are subscribed, we haven't requested anything
        assertThat(subscriber.getItems()).isEmpty();
        assertThat(subscriber.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("Scenario 1: Data should be emitted only when requested")
    void shouldEmitOnRequest() {
        CustomPublisher publisher = new CustomPublisher(10);
        CustomSubscriber<Integer> subscriber = new CustomSubscriber<>();

        publisher.subscribe(subscriber);
        
        subscriber.request(3);
        assertThat(subscriber.getItems()).containsExactly(1, 2, 3);
        
        subscriber.request(2);
        assertThat(subscriber.getItems()).containsExactly(1, 2, 3, 4, 5);
        
        subscriber.request(10); // Requesting more than remaining
        assertThat(subscriber.getItems()).hasSize(10);
        assertThat(subscriber.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Scenario 1: Cancellation should stop emission")
    void shouldStopOnCancel() {
        CustomPublisher publisher = new CustomPublisher(10);
        CustomSubscriber<Integer> subscriber = new CustomSubscriber<>();

        publisher.subscribe(subscriber);
        
        subscriber.request(2);
        assertThat(subscriber.getItems()).hasSize(2);
        
        subscriber.cancel();
        subscriber.request(5);
        
        // After cancel, no more items should be added even with new requests
        assertThat(subscriber.getItems()).hasSize(2);
    }
}
