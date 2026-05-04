package com.reactivelab.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    public Mono<StockQuote> getQuote(@PathVariable String symbol) {
        return stockService.getQuote(symbol);
    }

    @GetMapping(value = "/{symbol}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<StockQuote> getPriceStream(@PathVariable String symbol) {
        return stockService.getPriceStream(symbol);
    }

    @GetMapping(value = "/bulk", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<StockQuote> getBulkQuotes(@RequestParam(defaultValue = "100") int count) {
        return stockService.getAllQuotes(count);
    }
}
