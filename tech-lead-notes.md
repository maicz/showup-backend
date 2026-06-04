# tech lead notes

## isolate history cleanup mechanism [COMPLETED]
- [x] remove history cleanup configs from existing components (if it exists)
- [x] implement a dedicated fluxnova-cleanup-component which uses the fluxnova java api to periodically clear old deployments & hanging process instances.

## isolate the fluxnova front end and rest api [COMPLETED]
- [x] currently every component has the fluxnova monitoring front end and the fluxnova rest api as part of their dependencies.
- [x] this is not efficient
- [x] we need to have a dedicated component that does not process anything but only exposes the rest api and the monitoring tool.

## implement user registration and authentication [COMPLETED]
- [x] add spring-boot-starter-data-jpa, spring-boot-starter-validation, spring-boot-starter-security, and jwt dependency (com.auth0:java-jwt) to user-service
- [x] create user persistence model (entity and repository) to store users in PostgreSQL
- [x] implement registration REST API with validation (password strength, email format, unique checks)
- [x] implement login REST API with JWT generation
- [x] configure Spring Security to permit public access to registration/login endpoints and disable CSRF
- [x] integrate registration REST API with the user-registration-process BPMN workflow

## secure microservices using shared jwt validation [COMPLETED]
- [x] add spring-boot-starter-security and java-jwt dependencies to group-service
- [x] implement JwtAuthenticationFilter to intercept, validate JWTs, and populate Spring SecurityContext
- [x] configure security filter chain in group-service to protect all domain REST endpoints

## implement group service features and persistence [COMPLETED]
- [x] add spring-boot-starter-data-jpa, spring-boot-starter-validation, and postgres dependencies to group-service
- [x] create Group and Membership database entities and repositories
- [x] implement REST controllers for group creation and membership management (add/remove members)
- [x] secure group-service endpoints so only authenticated users (extracted from JWT) can create groups or manage memberships
- [x] integrate group operations with group-lifecycle-process BPMN workflow
- [x] create comprehensive integration tests for group-service