package com.reactivelab.webclient.controller;

import com.reactivelab.webclient.model.GlobalEvent;
import com.reactivelab.webclient.service.ReactiveOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final ReactiveOrchestrator orchestrator;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<GlobalEvent> getEventsStream() {
        return orchestrator.getEventsStream();
    }
}
