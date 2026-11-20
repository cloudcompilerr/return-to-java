# Progress — Java Return Tutor

_Single source of truth. Tutor reads this at session start and updates it at session end._

## Environment (verified 2026-11-20)

| Item | Version | Status |
|---|---|---|
| JDK | Amazon Corretto 25.0.5.1 (default); Temurin 25.0.4.1 also installed | OK |
| Maven | 3.9.16 via `./mvnw` (wrapper 3.3.4) | OK |
| Docker | CLI installed, **daemon not running** | Needed from M6 (Day 8): start Docker Desktop before then |
| Git | repo initialised, branch `main` | OK |

## Pinned stack (Maven Central, checked 2026-11-20)

| Component | Pinned | Notes |
|---|---|---|
| Spring Boot | **4.2.0** | Newest GA (published 2026-11-19). Fallback: 4.1.3 if a 4.2.0 regression bites |
| Spring Framework | 7.1.0 | Managed by Boot 4.2.0 |
| JUnit (Jupiter) | 6.1.3 | Boot 4.x manages JUnit 6, not 5; Jupiter API unchanged |
| AssertJ | 3.27.7 | |
| Mockito | 5.24.0 | Loaded as `-javaagent` in surefire (JEP 451) |
| Testcontainers | 2.0.5 | Boot-managed; used from M6 |
| Hibernate ORM | 7.4.15.Final | Boot-managed |
| Flyway | 12.11.0 | Boot-managed |
| Kafka client | 4.4.0 | Boot-managed |
| PostgreSQL JDBC | 42.7.14 | Boot-managed; server image 16+ |
| Micrometer | 1.18.0 | Boot-managed |

Toolchain smoke test: `./mvnw verify` → `Tests run: 3, Failures: 0, Errors: 0, Skipped: 0` (2026-11-20).

## Time budget

Confirmed 2026-11-20: ~90 min weekdays, ~3 h weekend days. Day 1 = Fri 2026-11-20 → Day 20 = Wed 2026-12-09.

## Mission status

| # | Mission | Planned day | Status | Score (C/D/Cc/T/R/P/Pr) |
|---|---|---|---|---|
| M1 | Order domain model | 1 | **issued** 2026-11-20 (57 failing tests) | |
| M2 | Order analytics engine | 2–3 | not started | |
| M3 | Thread-safe LRU cache with TTL | 4 | not started | |
| M4 | Inventory reservation & quote fan-out | 5 | not started | |
| M5 | Order API (Spring Boot 4) | 6–7 | not started | |
| M6 | Persistence (JPA, Flyway, Testcontainers) | 8–9 | not started | |
| M7 | SQL kata | 10 | not started | |
| M8 | Resilient downstream calls | 11 | not started | |
| M9 | Kafka + transactional outbox | 12–13 | not started | |
| M10 | DynamoDB read model | 14–15 | not started | |
| M11 | Order saga | 16 | not started | |
| M12 | Ship it | 17–18 | not started | |
| — | Consolidation | 19 | — | |
| — | Capstone mock round | 20 | — | |

Rubric key: C = Correctness, D = Design, Cc = Concurrency, T = Tests, R = Readability/idiom, P = Performance, Pr = Production readiness. 3 = Senior SDE bar.

## Weak spots

_(none yet; any rubric score of 1–2 lands here)_

## Drill queue

_(empty)_

## Session log

- 2026-11-20 — `onboard`: verified env, pinned versions, scaffolded root pom + `katas/` module + wrapper, smoke test green.
- 2026-11-20 — time budget confirmed; `next`: M1 issued. Acceptance tests pre-validated against a throwaway solution (60/60 green), solution discarded.
