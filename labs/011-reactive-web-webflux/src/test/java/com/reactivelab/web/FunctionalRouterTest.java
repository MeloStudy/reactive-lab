package com.reactivelab.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import({StockRouter.class, StockHandler.class, StockService.class})
public class FunctionalRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testFunctionalQuote_Success() {
        webTestClient.get()
                .uri("/functional/stocks/MSFT")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Execution-Time")
                .expectBody()
                .jsonPath("$.symbol").isEqualTo("MSFT");
    }

    @Test
    void testFunctionalQuote_NotFound() {
        webTestClient.get()
                .uri("/functional/stocks/INVALID")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFunctionalStream() {
        webTestClient.get()
                .uri("/functional/stocks/GOOGL/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM);
    }
}
