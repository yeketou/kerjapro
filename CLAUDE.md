# CLAUDE.md

This file provides guidance to Claude Code when working with the KerjaPro codebase.

## Project Overview

**KerjaPro** is a construction subcontractor marketplace platform targeting Malaysia and Southeast Asia.
It connects main contractors with subcontractors, enabling work package management, expertise-based
discovery (including brand/product certifications), appointment booking, and verified ratings.

**Tech stack:** Java 21, Spring Boot 3.2.5, Spring Cloud 2023.0.1, PostgreSQL 16, Kafka, Keycloak 24,
Redis, React Native (Expo 51), TypeScript

## Services & Ports

| Module                      | Port | Responsibility                                        |
|-----------------------------|------|-------------------------------------------------------|
| `kerjapro-gateway`          | 8079 | Spring Cloud Gateway — routing, JWT validation        |
| `service-contractor`        | 8081 | Subcontractor profiles, skills, brand certifications  |
| `service-project`           | 8082 | Projects, work packages, assignments                  |
| `service-booking`           | 8083 | Appointments, calendar, booking state machine         |
| `service-review`            | 8084 | Ratings, structured reviews, reputation scoring       |
| `service-notification`      | 8085 | FCM push notifications, in-app alerts                 |
| `kerjapro-common`           | —    | Shared library: base entities, security, exceptions   |
| `kerjapro-shared-migration` | —    | Flyway SQL migrations                                 |
| `kerjapro-mobile`           | —    | Expo React Native app (iOS + Android)                 |

## Common Commands

### Backend Build & Run

```bash
# Build all modules
mvn clean install

# Build a single service
mvn -pl service-contractor clean install

# Run with local profile (no JWT)
mvn -pl service-contractor spring-boot:run

# Run without Kafka
mvn -pl service-contractor spring-boot:run -Dspring-boot.run.profiles=local-no-kafka
```

### Mobile App

```bash
cd kerjapro-mobile

# Install dependencies
npm install

# Start dev server
npx expo start

# Run on Android
npx expo start --android

# Run on iOS
npx expo start --ios
```

### Local Dev Infrastructure

PostgreSQL runs in **Docker** (port 5433). All other services also run via Docker Compose:

```bash
# Start all infra
docker compose up -d
```

Key local credentials:
- Keycloak: `http://localhost:8080`, admin / admin_pass
- Kafka UI: `http://localhost:8091`
- MinIO console: `http://localhost:9001`
- Mailpit UI: `http://localhost:8025`
- Redis: port 6380, password `kerjapro_redis_pass`
- PostgreSQL: port 5433, db `kerjapro`, user `kerjapro`, pass `kerjapro_pass`

Swagger UI: `http://localhost:{port}/swagger-ui.html`

## Architecture

### User Roles
- `MAIN_CONTRACTOR` — creates projects, breaks work into packages, searches & books subcontractors
- `SUBCONTRACTOR` — manages profile, brand certifications, portfolio, responds to bookings
- `PLATFORM_ADMIN` — manages users, verifies certifications, manages brand partnerships

### Key Domain Concepts

#### Subcontractor Profile
Central entity in `service-contractor`. Contains:
- Trade specializations with `TradeCategory` enum
- Brand certifications (verified by platform/brand)
- Portfolio items with photos
- Subscription tier (FREE / PRO / PREMIUM)
- CIDB registration (Malaysian regulatory requirement)

#### Work Package
A scoped unit of work within a project. Main contractors break projects into packages by trade
(e.g., bathroom, electrical). Each package can specify a required brand.

#### Booking State Machine
`PENDING → CONFIRMED → COMPLETED`
`PENDING → DECLINED`
`CONFIRMED → CANCELLED`

#### Review
Created only after a booking is `COMPLETED`. Structured ratings:
- Overall, Workmanship, Punctuality, Communication, Brand Knowledge

### Database
Single PostgreSQL database (not schema-per-tenant — KerjaPro is not multi-tenant).
All migrations in `kerjapro-shared-migration/src/main/resources/db/migration/public/`.

### Spring Profiles

| Profile        | Use case                      |
|----------------|-------------------------------|
| `local`        | Dev — no JWT, all endpoints open |
| `local-no-kafka` | Dev without Kafka            |
| `prod`         | Full JWT via Keycloak          |

### Mobile App Structure

```
kerjapro-mobile/
├── app/
│   ├── _layout.tsx              # Root layout, providers
│   ├── index.tsx                # Role-based redirect
│   ├── auth/                    # Login, register, onboarding
│   ├── main-contractor/         # MC dashboard, projects, search, bookings
│   └── subcontractor/           # Sub dashboard, profile, calendar, reviews
├── components/
│   ├── common/                  # Shared UI components
│   └── contractor/              # Subcontractor-specific components
├── hooks/                       # Custom React hooks
├── services/                    # API service layer (axios)
├── store/                       # Zustand state stores
├── types/                       # TypeScript type definitions
└── constants/                   # API URLs, trade categories, etc.
```

## Key Conventions

- **Money:** Always `NUMERIC(15,2)` — never FLOAT
- **Timestamps:** `TIMESTAMPTZ` in UTC; display in `Asia/Kuala_Lumpur`
- **IDs:** UUID everywhere
- **Soft delete:** `deleted_at` column — never hard delete
- **Ratings:** `NUMERIC(3,2)` — e.g. 4.75
- **No multi-tenancy:** Single schema, unlike CommerceFlow

## MVP Build Order

1. Auth + role-based onboarding (Keycloak + Expo)
2. `service-contractor` — profile CRUD, trade specializations
3. Brand certifications — upload, verify flow
4. Portfolio — photo upload via MinIO
5. Search & filter (by trade, brand, rating, location)
6. `service-project` — project & work package management
7. `service-booking` — appointment booking state machine
8. `service-review` — post-completion reviews
9. `service-notification` — FCM push notifications
10. Subscription tiers & payment (Stripe / iPay88)
