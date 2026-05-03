package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import static org.assertj.core.api.Assertions.assertThat;

public class SmartSubscriberTest {

    @Test
    public void testManualDemandControl() {
        // GIVEN: A cold Flux with 5 items
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5);
        
        // AND: A custom subscriber that manages its own demand (request(1) loop)
        SmartSubscriber<Integer> subscriber = new SmartSubscriber<>();
        
        // WHEN: Subscribing
        numbers.subscribe(subscriber);
        
        // THEN: The subscriber should have received all items because it 
        // requested them one-by-one until the stream completed.
        // Rule: In synchronous Flux.just, the request/onNext cycle happens 
        // immediately during the subscribe() call.
        assertThat(subscriber.getReceivedCount())
            .as("Subscriber should have pulled and processed all 5 items")
            .isEqualTo(5);
    }
}
