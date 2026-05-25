package com.reactivelab.observability;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
class ObservabilityController {

    private final ObservabilityService observabilityService;

    @GetMapping("/api/call-downstream")
    public Mono<String> callDownstream(@RequestParam String url) {
        return observabilityService.callDownstream(url);
    }
}
