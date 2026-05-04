package com.reactivelab.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class StockHandler {

    private final StockService stockService;

    public StockHandler(StockService stockService) {
        this.stockService = stockService;
    }

    public Mono<ServerResponse> getQuote(ServerRequest request) {
        String symbol = request.pathVariable("symbol");
        return stockService.getQuote(symbol)
                .flatMap(quote -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(quote))
                .onErrorResume(StockNotFoundException.class, e -> ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getStream(ServerRequest request) {
        String symbol = request.pathVariable("symbol");
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(stockService.getPriceStream(symbol), StockQuote.class);
    }
}
