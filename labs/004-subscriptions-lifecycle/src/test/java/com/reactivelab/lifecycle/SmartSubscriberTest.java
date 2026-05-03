package com.reactivelab.lifecycle;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import static org.assertj.core.api.Assertions.assertThat;

public class SmartSubscriberTest {

    @Test
    public void testManualDemandControl() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5);
        SmartSubscriber<Integer> subscriber = new SmartSubscriber<>();
        
        numbers.subscribe(subscriber);
        
        // Since it requests 1 by 1 and it's synchronous Flux.just, 
        // it should have processed all 5 by the time subscribe() returns.
        assertThat(subscriber.getReceivedCount()).isEqualTo(5);
    }
}
