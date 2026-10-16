# Vercel portfolio demo

The responsive dashboard uses the original Java 17 Spring Boot encryption, mesh, duplicate detection and settlement services. Production tracks `codex/vercel-live-demo`; main is preserved for review in the pull request.

Vercel runs `Dockerfile.vercel` through the explicitly declared container service in `vercel.json`. No database credentials are required. The image runs tests during its build and listens on `PORT` (default 80).

## Browser sandbox on an autoscaling host

The public dashboard uses `POST /api/demo/session` with a bounded command history from sessionStorage. Each request reconstructs the synthetic accounts and replays that browser's actions through the real Java services under an instance-local lock, then returns a single consistent snapshot. This avoids dependence on instance affinity, another visitor's sandbox, or keys/caches from a previous cold start.

Packet IDs remain stable across replay. Encryption uses fresh ciphertext and the active instance's key; relay copies and repeated bridge uploads within the reconstructed scenario exercise the actual duplicate check and database settlement. Only synthetic sender, receiver and amount fields are stored in the browser, with a maximum of 60 actions. The demo PIN is always 1234 and is illustrative; no real bank PIN is accepted or stored in history.

The dashboard's mesh reset retains the history of balances and settlements. Start a fresh sandbox clears this tab's history and restores the initial synthetic balances. Browser history is temporary, editable and untrusted; this is a portfolio simulator, not a payment service or durable ledger. The original lower-level REST endpoints remain available for local API exploration and use instance-local temporary state.

## Local

Run `mvn test` then `mvn spring-boot:run`; open `http://localhost:8080`. The tests explicitly select dependency-injection listeners because they use no Mockito mocks and do not need runtime JVM agent attachment.

For a real persistent service, implement authentication, durable database storage and distributed idempotency separately. Database settings are read from `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DDL_AUTO`.
