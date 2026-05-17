package com.reactivelab.webclient.controller;

import com.reactivelab.webclient.model.UserDashboard;
import com.reactivelab.webclient.service.ReactiveOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class UserDashboardController {

    private final ReactiveOrchestrator orchestrator;

    @GetMapping("/{userId}")
    public Mono<UserDashboard> getUserDashboard(@PathVariable String userId) {
        return orchestrator.getUserDashboard(userId);
    }

    @GetMapping("/{userId}/full")
    public Mono<UserDashboard> getFullUserDashboard(@PathVariable String userId) {
        return orchestrator.getFullUserDashboard(userId);
    }
}
