package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class NotificationBusTest {

    @Test
    void shouldMulticastNotifications() {
        NotificationBus bus = new NotificationBus();

        // Sub 1
        StepVerifier v1 = StepVerifier.create(bus.listen())
                .expectNext("Hello")
                .thenCancel()
                .verifyLater();

        // Sub 2
        StepVerifier v2 = StepVerifier.create(bus.listen())
                .expectNext("Hello")
                .thenCancel()
                .verifyLater();

        bus.publish("Hello");

        v1.verify();
        v2.verify();
    }
}
