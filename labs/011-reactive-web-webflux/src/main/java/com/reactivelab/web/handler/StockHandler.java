package com.reactivelab.web.handler;

import com.reactivelab.web.model.StockQuote;
import com.reactivelab.web.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StockHandler {

    private final StockService stockService;

    public StockHandler(StockService stockService) {
        this.stockService = stockService;
    }

    public Mono<ServerResponse> getQuote(ServerRequest request) {
        String symbol = request.pathVariable("symbol");
        log.debug("Functional request for stock: {}", symbol);
        return stockService.getQuote(symbol)
                .flatMap(quote -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(quote));
    }

    public Mono<ServerResponse> getStream(ServerRequest request) {
        String symbol = request.pathVariable("symbol");
        log.info("Functional SSE stream request for: {}", symbol);
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(stockService.getPriceStream(symbol), StockQuote.class);
    }
}
