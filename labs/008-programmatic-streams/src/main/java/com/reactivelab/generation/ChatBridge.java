package com.reactivelab.generation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * Scenario 2 & 3: Bridging a push-based API with Overflow management.
 */
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
            ChatListener listener = new ChatListener() {
                @Override
                public void onMessage(String msg) {
                    sink.next(msg);
                }

                @Override
                public void onError(Throwable t) {
                    sink.error(t);
                }
            };

            service.register(listener);

            // Resource Cleanup: Crucial to prevent leaks
            sink.onDispose(() -> service.unregister(listener));
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
