# Hopper Docker & Dev Setup Plan

Plan to stand up a containerized local environment that lets us iterate on a new frontend while exercising the Spring Boot API (`build.gradle`, `src/main/java/...`) and the Go marketplace connector (`services/cmd/marketplace`). Scope is hobby-level reliability, favoring clarity over heavy infra.

## Goals & Guardrails
- Consistent `docker compose` workflow for API + DB + marketplace stub + future frontend.
- Keep secrets in env files, not images. Prefer simple `.env` + `docker compose config` for overrides.
- Optimize for fast inner loop: hot reload for frontend, quick rebuilds for backend/Go.
- Deliver minimal docs and helper scripts so one command starts the stack.
- Non-goals: k8s manifests, autoscaling, production observability.

## Target Container Topology
| Service | Source | Image Strategy | Ports | Notes |
| --- | --- | --- | --- | --- |
| `api` | Spring Boot app under `src/main/java` | Multi-stage Dockerfile (Temurin JDK 21 builder → JRE runtime) or `bootBuildImage` | 8080 → host 8080 | Mount optional volume for dev logs; profile via `SPRING_PROFILES_ACTIVE=dev` |
| `db` | PostgreSQL 15 | Official `postgres:15-alpine` | 5432 → host 5432 | Named volume for data, init SQL mounting `src/main/resources/db/migration` |
| `marketplace` | Go stub (`services/cmd/marketplace`) | Multi-stage Go build, `distroless` or `alpine` runtime | 8090 → host 8090 | Env vars for shared secret + log level |
| `frontend` | New Vite+React app (to be created) | Node 22 base w/ PNPM/Yarn, dev server mode | 5173 → host 5173 | Bind mount source for HMR |
| `seed` (optional) | Flyway CLI or slim container | `flyway/flyway` | n/a | Runs migrations/seed data on demand |

## Workstream Breakdown

### 1. Repo Prereqs & Shared Assets
1. Inventory secrets + config knobs already referenced in `README.md` and `dev-docs/DEVELOPMENT.md`.
2. Create `/env/.sample` folder with `api.env`, `db.env`, `frontend.env`, `marketplace.env`; add `env/.gitignore`.
3. Add base Makefile targets (`make dev-up`, `dev-down`, `api-shell`, etc.) calling `docker compose`.
4. Decide naming convention (`hopper-*`) for containers and network.

### 2. Compose Foundation
1. Author `docker-compose.yml` (v3.9) at repo root with services table above.
2. Define shared network `hopper-dev` and volumes (`pgdata`, optional `gradle-cache`).
3. Wire env files via `env_file:` entries and surface critical variables via `environment:` (e.g., `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/hopper`).
4. Add healthchecks for API (`/actuator/health`), marketplace (`/v1/health`), and Postgres (`pg_isready`).
5. Document override pattern (`docker-compose.override.yml`) for custom ports.

### 3. Spring API Image
1. Create `Dockerfile.api` (or `api/Dockerfile`) using Gradle wrapper:
   - Stage 1: `eclipse-temurin:21-jdk` → `./gradlew clean bootJar`.
   - Stage 2: `eclipse-temurin:21-jre-alpine` copying `build/libs/hopper-*.jar`.
   - Default profile `dev`, configure JVM opts via env.
2. Support dev-mode rebuilds:
   - Add Make target `make api-image` to rebuild.
   - Optionally mount source and run `./gradlew bootRun` inside container for hot reload (faster inner loop).
3. Update Compose to use build context, attach to `db` dependency, mount `data/` if H2 used.

### 4. PostgreSQL & Migration Flow
1. Add `docker/db/init.sql` for local roles/db creation aligning with `application-prod.properties`.
2. Configure named volume `pgdata` and bind mount migration scripts for visibility.
3. Add Flyway container (one-off) or Gradle task inside API container to run `flywayMigrate`.
4. Provide `scripts/wait-for-db.sh` or reuse Compose `depends_on` with healthcheck to ensure API starts after DB.
5. Document local backup/restore commands (simple `pg_dump`/`psql`).

### 5. Go Marketplace Connector
1. Create `services/marketplace/Dockerfile`:
   - Stage 1: `golang:1.23-alpine`, run `go build ./cmd/marketplace`.
   - Stage 2: `gcr.io/distroless/base-debian12` (or `alpine`) copying binary + CA certs.
2. Add configuration envs: `PORT`, `MARKETPLACE_SHARED_SECRET`, `LOG_LEVEL`.
3. Compose wiring: expose port 8090, healthcheck on `/v1/health`, optional bind mount for sample payloads (`services/samples`).
4. Provide curl smoke tests referencing new container.

### 6. Frontend Container Scaffold
1. Bootstrap `frontend/` (later) with Vite+React; plan Dockerfile now:
   - Base `node:22-alpine`, install deps, run `npm install` (or pnpm), default command `npm run dev -- --host`.
   - Bind mount repo `frontend/` for hot reload, map port 5173.
2. Define shared env contract:
   - `VITE_API_BASE_URL=http://api:8080/api`.
   - Optionally `VITE_MARKETPLACE_BASE_URL=http://marketplace:8090/v1`.
3. Compose service uses `depends_on: [api]`, enabling frontend to talk to container network names (`api`, `marketplace`).
4. Document `npm run build` + static export path to be served later by nginx if desired.

### 7. Developer Ergonomics & Docs
1. Extend `README.md` with "Docker Dev Stack" section: prerequisites, commands, port map, env file instructions.
2. Add `dev-docs/DOCKER.md` (or extend) describing architecture diagram + troubleshooting.
3. Provide sample data loader script (could run via Gradle task or container) populating products/listings.
4. Consider `.vscode/tasks.json` or JetBrains run configuration pointing to Compose for teammates.
5. Wire CI smoke (GitHub Actions) to build images (`api`, `marketplace`) and run unit tests inside containers.

## Validation Checklist
- `docker compose up` starts all four core containers, healthchecks green.
- Frontend hitting `http://localhost:5173` can call API via container hostname.
- `docker exec hopper-api ./gradlew test` works for on-demand tests.
- Database data survives stack restarts via named volume.
- Rebuilding API or Go service with code changes only requires `make dev-restart api` (target to recreate container).

## Open Questions & Decisions Needed
- Preferred frontend stack (React/Vite assumed) and package manager?
- Should we prioritize `bootBuildImage` (packs) over bespoke Dockerfile for API?
- Are we comfortable exposing Postgres port to host, or should we enforce container-only access?
- Any additional backing services (Redis, message broker) needed soon for marketplace sync?

Once decisions above are made, we can implement phases sequentially or in parallel (e.g., API + DB first, then marketplace + frontend) before starting UI work.
