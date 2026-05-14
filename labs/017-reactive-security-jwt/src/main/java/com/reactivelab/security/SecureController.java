package com.reactivelab.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class SecureController {

    @GetMapping("/secure/data")
    public Mono<String> getSecureData() {
        return Mono.just("Secure User Data");
    }

    @GetMapping("/admin/data")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> getAdminData() {
        return Mono.just("Secure Admin Data");
    }
}
