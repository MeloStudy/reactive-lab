package com.reactivelab.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtUtil jwtUtil;

    private String validUserToken;
    private String validAdminToken;

    @BeforeEach
    void setUp() {
        // Generate mock tokens for the test
        validUserToken = jwtUtil.generateToken("testuser", List.of("ROLE_USER"));
        validAdminToken = jwtUtil.generateToken("adminuser", List.of("ROLE_ADMIN"));
    }

    /**
     * SCENARIO 1: Stateless Authentication (Missing/Invalid Token)
     * Validates that requests without a Bearer token are rejected with 401 Unauthorized.
     */
    @Test
    void shouldReturn401WhenNoTokenProvided() {
        webTestClient.get()
                .uri("/api/secure/data")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * SCENARIO 1: Stateless Authentication (Valid Token)
     * Validates that requests with a valid Bearer token are accepted.
     */
    @Test
    void shouldReturn200WhenValidTokenProvided() {
        webTestClient.get()
                .uri("/api/secure/data")
                .header("Authorization", "Bearer " + validUserToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Secure User Data");
    }

    /**
     * SCENARIO 2: Reactive Role-Based Authorization
     * Validates that a user with ROLE_USER cannot access an endpoint requiring ROLE_ADMIN.
     */
    @Test
    void shouldReturn403WhenUserTriesToAccessAdminEndpoint() {
        webTestClient.get()
                .uri("/api/admin/data")
                .header("Authorization", "Bearer " + validUserToken)
                .exchange()
                .expectStatus().isForbidden();
    }

    /**
     * SCENARIO 2: Reactive Role-Based Authorization
     * Validates that a user with ROLE_ADMIN can access an endpoint requiring ROLE_ADMIN.
     */
    @Test
    void shouldReturn200WhenAdminTriesToAccessAdminEndpoint() {
        webTestClient.get()
                .uri("/api/admin/data")
                .header("Authorization", "Bearer " + validAdminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Secure Admin Data");
    }
}
