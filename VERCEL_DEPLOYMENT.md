# Vercel portfolio demo

This deployment preserves the Java 17 Spring Boot backend and replaces the basic dashboard with a responsive interface.

Import the `codex/vercel-live-demo` branch into Vercel. The `Dockerfile.vercel` uses Vercel's Container Runtime. No build command or database credentials are required for the sandbox. The image runs the existing tests during its build and listens on `PORT` (default 80).

## Sandbox behavior

- Only synthetic accounts and balances are included. There are no bank connections or real payments.
- H2, mesh state, keys and duplicate caches are temporary and local to a running instance. They reset on restart and are not synchronized across scaled instances. This is a portfolio demonstration, not a production payment service.
- The dashboard's reset clears mesh packets and duplicate hashes; it retains account balances and history.
- The PIN is illustrative; the demo does not authenticate a real bank PIN. Never enter a real PIN.
- For a durable deployment, configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DDL_AUTO=update`, and implement durable distributed mesh/idempotency state before scaling.

## Local

Run `mvn test` then `mvn spring-boot:run`; open `http://localhost:8080`.

The test class explicitly selects Spring dependency injection listeners because these tests use no Mockito mocks and do not need runtime JVM agent attachment.
