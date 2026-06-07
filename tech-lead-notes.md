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

## implement event service features and persistence [COMPLETED]
- [x] add spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, spring-boot-starter-security, and jwt dependency to event-service
- [x] create Event database entity and repository
- [x] implement REST controllers for event creation and retrieval
- [x] secure event-service endpoints using shared JWT validation
- [x] implement BPMN delegates logic for event metadata validation (ProcessEventDelegate), permission checking, private setup, and public publishing
- [x] create comprehensive integration tests for event-service

## implement rsvp service features and persistence [COMPLETED]
- [x] add spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-validation, spring-boot-starter-security, and jwt dependency to rsvp-service
- [x] create Rsvp database entity and repository
- [x] implement REST controllers for RSVP creation/update, event RSVP counts summary, and user RSVP list
- [x] secure rsvp-service endpoints using shared JWT validation
- [x] implement recordRsvpDelegate logic for recording RSVPs in the BPMN process
- [x] create comprehensive integration tests for rsvp-service

## implement api gateway and authentication service [COMPLETED]
- [x] create dedicated api-gateway service using Spring Cloud Gateway to proxy requests to appropriate microservices
- [x] create dedicated auth-service validating credentials and generating JWT tokens
- [x] implement token validation endpoint (/api/auth/validate) in auth-service
- [x] create comprehensive unit tests for auth-service and api-gateway

## implement synchronous grpc communication [COMPLETED]
- [x] define group.proto service contract for verifying user group membership
- [x] configure gRPC server in group-service on port 9082 and implement GroupGrpcServiceImpl
- [x] configure gRPC client in event-service targeting group-service gRPC server
- [x] integrate gRPC call in VerifyEventPermissionsDelegate inside event-service to verify creator permissions dynamically
- [x] write gRPC service unit tests in group-service (GroupGrpcServiceTest)