# Concept: Reactive Security & Identity

Securing a WebFlux application presents a unique challenge: we can no longer rely on `ThreadLocal` variables to store the current user's security context. 

## The ThreadLocal Problem in Security

In traditional Servlet-based Spring Security (Spring MVC), when a request is received, a thread is assigned from a thread pool. The `SecurityContextPersistenceFilter` reads the session (or token) and populates the `SecurityContextHolder`, which uses `ThreadLocal` under the hood. Any downstream code (controllers, services) can simply call `SecurityContextHolder.getContext().getAuthentication()` to see who is logged in.

In **Reactive WebFlux**, a single Event Loop thread handles thousands of interleaved requests. If we put the `Authentication` object into a `ThreadLocal`, it would instantly leak into other users' requests being processed by the same thread.

## The Solution: ReactiveSecurityContextHolder

Spring Security WebFlux replaces `ThreadLocal` with Project Reactor's `Context` (explored in LAB-016). 

The `ReactiveSecurityContextHolder` is a utility that writes to and reads from the Reactor `Context`. 
When the `AuthenticationWebFilter` successfully authenticates a request (e.g., via a JWT), it doesn't save it to a thread. Instead, it injects it into the Reactor Context using `.contextWrite()`.

Because the Context is tied to the **Subscription** (the specific HTTP request pipeline), the security state is safely isolated, even as the request jumps across dozens of threads (`Schedulers`) during its lifecycle.

## The Reactive Security Components

1. **SecurityWebFilterChain**: The reactive equivalent of `SecurityFilterChain`. It is built using a fluent DSL to define which endpoints require authentication and which require specific roles.
2. **ServerAuthenticationConverter**: Intercepts the incoming `ServerWebExchange` (HTTP request) and extracts the credential (e.g., parsing the `Authorization: Bearer <token>` header).
3. **ReactiveAuthenticationManager**: Takes the extracted credential, validates it (e.g., verifying the JWT signature), and returns a fully populated `Authentication` object containing the user's roles (`GrantedAuthority`).
4. **@EnableReactiveMethodSecurity**: Enables annotations like `@PreAuthorize("hasRole('ADMIN')")`. Under the hood, this creates an AOP interceptor that uses `ReactiveSecurityContextHolder.getContext()` to verify the current user's roles before executing the method.

## Stateless vs Stateful

This lab focuses on **Stateless Authentication** using JSON Web Tokens (JWT). Because JWTs contain all necessary user information (username, expiration, roles) cryptographically signed, the server does not need to query a database or maintain a server-side Session to authenticate the request. This perfectly complements the high-throughput, horizontally scalable nature of Reactive architectures.
