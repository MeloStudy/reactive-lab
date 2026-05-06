package com.reactivelab.orchestration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * Scenario 2: Demonstrates the difference between merge (interleaved) and concat (sequential).
 * 
 * In a real-world scenario, you might want to fetch data from multiple social feeds
 * as fast as possible (merge) or load a cache before refreshing from a remote source (concat).
 */
@Slf4j
public class SocialFeedService {

    /**
     * Merges feeds from multiple sources.
     * 
     * [PEDAGOGICAL NOTE]:
     * Flux.merge is EAGER. It subscribes to all sources simultaneously.
     * The resulting stream will contain items as they arrive, which means they
     * will be INTERLEAVED. This is ideal for performance when order doesn't matter.
     * 
     * @param twitterFeed The first source (e.g., Twitter)
     * @param instagramFeed The second source (e.g., Instagram)
     * @return A merged Flux of social posts.
     */
    public Flux<String> combineFeedsEagerly(Flux<String> twitterFeed, Flux<String> instagramFeed) {
        return Flux.merge(twitterFeed, instagramFeed)
                .doOnNext(post -> log.info("Emitting post from merged feed: {}", post));
    }

    /**
     * Concatenates sources in a strict sequence.
     * 
     * [PEDAGOGICAL NOTE]:
     * Flux.concat is LAZY. It subscribes to the first source and WAITS for it
     * to complete before subscribing to the next one.
     * This guarantees that all items from source1 appear before any item from source2.
     * Ideal for dependent steps like (1) Load Cache -> (2) Fetch Remote.
     * 
     * @param localCache The first source to consume entirely.
     * @param remoteSource The second source to consume after the first one finishes.
     * @return A concatenated Flux.
     */
    public Flux<String> combineFeedsSequentially(Flux<String> localCache, Flux<String> remoteSource) {
        return Flux.concat(localCache, remoteSource)
                .doOnNext(post -> log.info("Emitting post from sequential feed: {}", post));
    }
}
