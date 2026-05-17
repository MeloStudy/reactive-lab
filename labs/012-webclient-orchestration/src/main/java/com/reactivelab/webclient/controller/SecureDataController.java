package com.reactivelab.webclient.controller;

import com.reactivelab.webclient.service.ReactiveOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/secure-data")
@RequiredArgsConstructor
public class SecureDataController {

    private final ReactiveOrchestrator orchestrator;

    @GetMapping("/{id}")
    public Mono<String> getSecureData(@PathVariable String id) {
        return orchestrator.getSecureData(id);
    }
}
