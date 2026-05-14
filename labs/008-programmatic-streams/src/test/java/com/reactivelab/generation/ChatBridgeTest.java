package com.reactivelab.generation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class ChatBridgeTest {

    @Test
    void shouldBridgeMessages() {
        AtomicReference<ChatBridge.ChatListener> listenerRef = new AtomicReference<>();
        ChatBridge.ExternalChatService mockService = new ChatBridge.ExternalChatService() {
            @Override
            public void register(ChatBridge.ChatListener listener) {
                listenerRef.set(listener);
            }
        };

        ChatBridge bridge = new ChatBridge();

        StepVerifier.create(bridge.bridge(mockService, FluxSink.OverflowStrategy.BUFFER))
                .then(() -> listenerRef.get().onMessage("Hello"))
                .then(() -> listenerRef.get().onMessage("World"))
                .expectNext("Hello", "World")
                .thenCancel()
                .verify();
    }

    @Test
    void shouldHandleOverflowWithDrop() {
        AtomicReference<ChatBridge.ChatListener> listenerRef = new AtomicReference<>();
        ChatBridge.ExternalChatService mockService = new ChatBridge.ExternalChatService() {
            @Override
            public void register(ChatBridge.ChatListener listener) {
                listenerRef.set(listener);
            }
        };

        ChatBridge bridge = new ChatBridge();

        // Request 1, emit 3. With DROP, Msg2 and Msg3 should be GONE.
        StepVerifier.create(bridge.bridge(mockService, FluxSink.OverflowStrategy.DROP), 1)
                .then(() -> {
                    listenerRef.get().onMessage("Msg1"); // Consumed
                    listenerRef.get().onMessage("Msg2"); // Dropped
                    listenerRef.get().onMessage("Msg3"); // Dropped
                })
                .expectNext("Msg1")
                .thenRequest(1)
                .expectNoEvent(Duration.ofMillis(500)) // Nothing should arrive
                .thenCancel()
                .verify();
    }

    @Test
    void shouldUnregisterOnCancellation() {
        AtomicBoolean unregisterCalled = new AtomicBoolean(false);
        ChatBridge.ExternalChatService mockService = new ChatBridge.ExternalChatService() {
            @Override
            public void register(ChatBridge.ChatListener listener) {
                // Do nothing
            }

            @Override
            public void unregister(ChatBridge.ChatListener listener) {
                unregisterCalled.set(true);
            }
        };

        ChatBridge bridge = new ChatBridge();

        StepVerifier.create(bridge.bridge(mockService, FluxSink.OverflowStrategy.BUFFER))
                .expectSubscription()
                .thenCancel()
                .verify();

        assertThat(unregisterCalled.get()).isTrue();
    }
}
