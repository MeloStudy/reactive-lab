# LAB-017: Reactive Security & Identity (OAuth2/JWT)

In this lab, you will learn how to secure a Spring WebFlux application without blocking the event loop. You will implement stateless JWT authentication and role-based authorization using Spring Security's reactive constructs.

## Prerequisites
- Completed LAB-011 (WebFlux Foundations).
- Completed LAB-016 (Reactive Context Propagation).

---

## Scenario 1: Stateless JWT Authentication

We will intercept incoming requests, extract a Bearer token, validate its cryptographic signature, and populate the Reactor Context with the authenticated user.

### Step 1: The Authentication Converter
The `ServerAuthenticationConverter` is responsible for extracting the raw token from the HTTP headers.
Observe `bearerTokenConverter()` in `SecurityConfig.java`. It looks for the `Authorization` header and strips the `Bearer ` prefix.

### Step 2: The Authentication Manager
The `ReactiveAuthenticationManager` verifies the token.
Observe `jwtAuthenticationManager()` in `SecurityConfig.java`. It uses `JwtUtil` to parse the token. If valid, it constructs an `Authentication` object (a `UsernamePasswordAuthenticationToken`) containing the user's roles.

### Step 3: The Filter Chain
Observe the `springSecurityFilterChain()`. We register our custom `AuthenticationWebFilter` and configure authorization rules.

🔗 **Traceable Implementation**: [SecurityConfig.java](src/main/java/com/reactivelab/security/SecurityConfig.java) | [SecurityIntegrationTest.java](src/test/java/com/reactivelab/security/SecurityIntegrationTest.java)

---

## Scenario 2: Reactive Role-Based Authorization

Once the user is authenticated, we can restrict access based on their roles.

### Step 1: Enable Method Security
In `SecurityConfig.java`, we added `@EnableReactiveMethodSecurity`.

### Step 2: Apply PreAuthorize
In `SecureController.java`, observe the `/api/admin/data` endpoint. It is protected by `@PreAuthorize("hasRole('ADMIN')")`.

If a user with only `ROLE_USER` accesses this endpoint, the reactive security interceptor will immediately return a `Mono.error(AccessDeniedException)`, which translates to a `403 Forbidden` HTTP status.

🔗 **Traceable Implementation**: [SecureController.java](src/main/java/com/reactivelab/security/SecureController.java) | [SecurityIntegrationTest.java](src/test/java/com/reactivelab/security/SecurityIntegrationTest.java)

---

## Command Dissection

| Component | Rationale | Behavior |
| :--- | :--- | :--- |
| `SecurityWebFilterChain` | **Reactive Security DSL** | Replaces the Servlet-based `SecurityFilterChain`. Configures non-blocking filters. |
| `ServerAuthenticationConverter` | **Credential Extraction** | Extracts credentials (like JWTs) from the `ServerWebExchange`. |
| `ReactiveAuthenticationManager` | **Credential Validation** | Validates credentials reactively and returns a populated `Authentication` object. |
| `@EnableReactiveMethodSecurity` | **AOP Authorization** | Enables reactive AOP interceptors for `@PreAuthorize` annotations using the Reactor Context. |

---

## 🧠 Self-Assessment

<details>
<summary>1. Why can't we use <code>SecurityContextHolder.getContext()</code> (ThreadLocal) in WebFlux?</summary>
Because a single Event Loop thread processes many requests concurrently. A <code>ThreadLocal</code> would leak security credentials across different users' requests. WebFlux uses <code>ReactiveSecurityContextHolder</code> which stores the context safely inside the Reactor <code>Context</code> tied to the specific HTTP pipeline.
</details>

<details>
<summary>2. What is the role of the <code>ReactiveAuthenticationManager</code>?</summary>
It takes an unauthenticated token (extracted by the Converter), validates it (e.g., verifying a JWT signature or checking a database), and returns a fully populated, authenticated <code>Authentication</code> object with the user's roles.
</details>

<details>
<summary>3. How does <code>@PreAuthorize</code> know who the current user is in a non-blocking method?</summary>
The AOP interceptor backing <code>@PreAuthorize</code> calls <code>ReactiveSecurityContextHolder.getContext()</code> to extract the authentication details from the upstream Reactor Context before allowing the method to execute.
</details>

---

## Verification (Running Locally)

To verify the implementation, run the automated integration tests:
```bash
mvn clean test -pl labs/017-reactive-security-jwt
```
