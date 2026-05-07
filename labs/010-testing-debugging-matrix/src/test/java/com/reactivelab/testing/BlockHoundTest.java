package com.reactivelab.testing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockHound;
import reactor.test.StepVerifier;

class BlockHoundTest {

    @BeforeAll
    static void setup() {
        BlockHound.install();
    }

    @Test
    void testDetectIllegalBlockingCall() {
        BlockHoundDetector detector = new BlockHoundDetector();

        StepVerifier.create(detector.performIllegalBlock())
                .expectErrorMatches(t -> t.getClass().getName().contains("BlockingOperationError"))
                .verify();
    }
}
