# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**museCraft** is an AI-powered creator economy platform (early-stage, currently in architecture/initialization phase). The backend is planned as 5 Spring Boot microservices; `src/main/java` is currently empty. The portfolio/architecture docs live in `docs/index.html`.

## Build & Run Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.ClassName"

# Run application
./gradlew bootRun

# Local full-stack dev (once docker-compose.yml is added)
docker-compose up
```

## Planned Architecture

### Microservices (Spring Boot 3.x, Java)

| Service | Port | Responsibility |
|---|---|---|
| User Service | :8081 | Auth, profiles |
| Brand Service | :8082 | AI brand identity generation |
| Subscription Service | :8083 | Tier management, access control |
| Settlement Service | :8084 | Monthly creator payouts (Spring Batch) |
| Notification Service | :8085 | Email/push via Kafka events |

All services sit behind **Spring Cloud Gateway** (:8080), which handles JWT validation and rate limiting.

### Key Technologies
- **Spring Boot 3.x + Spring Security + JWT** — Access token: 15 min, Refresh token: 7 days (Redis-backed for revocation)
- **Spring Data JPA + QueryDSL** — Use Fetch Joins to avoid N+1; QueryDSL for type-safe dynamic queries
- **Spring Batch** — Subscription expiry (midnight, 1000-item chunks), monthly settlements
- **Apache Kafka** — Newsletter delivery (sync 45 min → async 3 min for 10K subscribers)
- **Redis (ElastiCache)** — Subscription state cache (1-hour TTL, 97% DB load reduction), JWT store, distributed locks
- **MySQL 8.0 (RDS)** — Primary DB; JSON columns for brand identity, composite indexes for stats queries
- **Claude API** — Brand identity generation (slogan, story, color palette as structured JSON)
- **DALL-E** — Logo generation, stored in S3 with daily limits
- **AWS S3 + CloudFront** — File storage; Presigned URLs (5-min expiry) for subscription-gated content
- **Next.js 14 + TypeScript + Tailwind + React Query** — Frontend (separate repo or not yet scaffolded)

### Subscription Model
3 tiers (Free, Basic, Premium) enforced via **AOP** across services. Subscription state is cached in Redis.

## Branch & CI/CD Strategy

- `feature/xxx` → local development only, no CI/CD
- `develop` → PR triggers CI (build + test via GitHub Actions)
- `main` → PR merge triggers full CI/CD: build → Docker image push to DockerHub → SSH deploy to EC2

## Infrastructure (AWS Free Tier)
EC2 (t2.micro) + RDS (MySQL) + ElastiCache + S3 (5GB) + CloudFront. Docker multi-stage builds used for image size optimization (JDK build → JRE runtime).
