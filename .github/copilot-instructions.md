# Copilot Instructions for Tennis Sheet

This file gives focused, project-specific guidance for AI coding agents working on this repository.

Purpose
- Small Spring Boot backend (package: `com.gardey.tennis_sheet`) that manages tennis courts.

Quick facts
- Build: Maven wrapper (`./mvnw`). Common commands: `./mvnw clean package`, `./mvnw test`, `./mvnw spring-boot:run`.
- DB: Flyway migrations in `src/main/resources/db/migration` (H2 in-memory by default; see `application.properties`).
- App entry: `TennisSheetApplication`.

Architecture / patterns to follow
- Layered design: Controller -> Service -> Repository (Spring Data JPA) -> Model. See `controllers/CourtController.java`, `services/CourtService.java`, `repositories/CourtRepository.java`, `models/Court.java`.
- DTOs: immutable DTOs with constructor-based fields (example: `dtos/CreateCourtRequestDTO.java`, `dtos/CreateCourtResponseDTO.java`). Match JSON -> constructor mapping when adding new DTOs.
- Exception handling: application-wide handler in `exceptions/GlobalExceptionHandler.java` returns `ErrorResponse` for domain errors (e.g., `CourtNameAlreadyExistsException`). Use that pattern for new domain errors.
- Database schema is managed by Flyway. Do not rely on `hibernate.ddl-auto` (it's set to `none`). Add migrations to `src/main/resources/db/migration` for schema changes.

Developer workflows (explicit)
- Run tests locally: `./mvnw test`.
- Run app locally: `./mvnw spring-boot:run` (app listens on :8080). Use `requests.http` for quick API requests.
- Docker: `docker-compose up` is provided for containerized runs; prefer that only if infrastructure changes are required.

Conventions & gotchas
- Use package `com.gardey.tennis_sheet` and existing subpackages.
- Repositories extend `JpaRepository`; add query methods using Spring Data naming conventions (e.g., `existsByName`).
- DTOs are small, constructor-based immutable objects. When adding new DTOs ensure Jackson can deserialize (either add a default constructor or annotate constructors appropriately).
- Transactions: service methods annotate with `@Transactional` and use readOnly for queries — follow existing use in `CourtService`.

Tests & mocks
- Controller tests use `@WebMvcTest` and mock the service layer. Follow this pattern for controller-level unit tests.
- Integration tests (if added) should run with the Flyway migrations and H2; mirror `application.properties` settings.

Files to inspect when changing behavior
- API surface: `src/main/java/com/gardey/tennis_sheet/controllers`.
- Business rules: `src/main/java/com/gardey/tennis_sheet/services`.
- Persistence: `src/main/java/com/gardey/tennis_sheet/repositories` and `src/main/resources/db/migration`.
- Error handling: `src/main/java/com/gardey/tennis_sheet/exceptions`.

When opening a PR
- Run `./mvnw test` and include test results in PR description if relevant.
- If schema changes are required, add a Flyway migration and include a brief note why.

Examples
- Create court flow: POST `/api/courts` -> `CourtController.create()` -> `CourtService.createCourt()` checks `existsByName()` -> save via `CourtRepository` -> returns `CreateCourtResponseDTO`.
- Example request in repo: `requests.http` (root).

If anything here is unclear, ask for the specific area (build, DB, DTOs, tests) and I will expand.
