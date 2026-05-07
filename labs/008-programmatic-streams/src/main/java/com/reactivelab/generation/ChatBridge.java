package com.reactivelab.generation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * Scenario 2: Bridging a push-based API with Overflow management.
 * Demonstrates the 'push' model where external events drive emissions,
 * and the importance of lifecycle cleanup via onDispose.
 */
@Slf4j
public class ChatBridge {

    public interface ChatListener {
        void onMessage(String msg);

        void onError(Throwable t);
    }

    /**
     * Wraps a listener-based service into a Flux.
     * Rule: Flux.create allows multiple emissions and handles Push sources.
     */
    public Flux<String> bridge(ExternalChatService service, FluxSink.OverflowStrategy strategy) {
        return Flux.create(sink -> {
            log.info("New subscription established. Registering ChatListener.");
            
            ChatListener listener = new ChatListener() {
                @Override
                public void onMessage(String msg) {
                    log.debug("Received message from external service: {}", msg);
                    sink.next(msg);
                }

                @Override
                public void onError(Throwable t) {
                    log.error("Error from external chat service", t);
                    sink.error(t);
                }
            };

            service.register(listener);

            // Resource Cleanup: Crucial to prevent leaks when subscriber cancels
            sink.onDispose(() -> {
                log.info("Subscription disposed. Unregistering ChatListener to prevent memory leaks.");
                service.unregister(listener);
            });
        }, strategy);
    }

    // Mock Service for exercise
    public static class ExternalChatService {
        public void register(ChatListener listener) {
            // mocked register service
        }

        public void unregister(ChatListener listener) {
            // mocked unregister service
        }
    }
}
