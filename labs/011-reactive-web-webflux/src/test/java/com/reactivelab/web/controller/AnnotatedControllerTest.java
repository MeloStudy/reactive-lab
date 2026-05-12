package com.reactivelab.web.controller;

import com.reactivelab.web.model.StockQuote;
import com.reactivelab.web.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = StockController.class)
@Import(StockService.class)
class AnnotatedControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetQuote_Success() {
        webTestClient.get()
                .uri("/stocks/AAPL")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.symbol").isEqualTo("AAPL")
                .jsonPath("$.price").exists();
    }

    @Test
    void testGetQuote_NotFound() {
        webTestClient.get()
                .uri("/stocks/UNKNOWN")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Stock not found: UNKNOWN");
    }

    @Test
    void testGetPriceStream() {
        webTestClient.get()
                .uri("/stocks/AAPL/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .returnResult(StockQuote.class)
                .getResponseBody()
                .take(3)
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void testGetBulkQuotes_NDJSON() {
        webTestClient.get()
                .uri("/stocks/bulk?count=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
                .expectBodyList(StockQuote.class)
                .hasSize(5);
    }
}
