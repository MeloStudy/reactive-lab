package com.reactivelab.webclient.controller;

import com.reactivelab.webclient.service.ReactiveOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ReactiveOrchestrator orchestrator;

    @GetMapping("/{productId}")
    public Mono<String> getInventoryStatus(@PathVariable String productId) {
        return orchestrator.getInventoryStatus(productId);
    }
}
