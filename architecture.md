# Showup — Backend Architecture Outline

This document outlines the multi-project backend architecture for **Showup**, built on Spring Boot and the Finos Fluxnova BPM platform.

---

## Services & Ports Catalog

The system consists of independent Maven projects configured to run concurrently.

### Core Domain Services

| Directory / Module | Service Port | Main Responsibility | Primary BPMN Process |
| :--- | :---: | :--- | :--- |
| **[user-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/user-service)** | `8086` | Registration, login, profiles, friendships/contacts | `user-service-process`, `user-registration-process` |
| **[group-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/group-service)** | `8082` | Groups (e.g. "Hiking Crew"), membership, roles (admin/member) | `group-lifecycle-process` |
| **[event-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/event-service)** | `8083` | Create/edit events, scheduling, venue, recurrence rules | `event-service-process` |
| **[rsvp-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/rsvp-service)** | `8085` | Attendance responses, headcount, waitlists | `rsvp-service-process` |
| **[notification-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/notification-service)** | `8084` | Email/push/in-app reminders, digest scheduling | `notification-service-process` |

### Supporting & Sandbox Services

| Directory / Module | Service Port | Main Responsibility | Primary BPMN Process |
| :--- | :---: | :--- | :--- |
| **[cost-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/cost-service)** | `8087` | Per-event expenses, splitting logic, settlement tracking | `cost-service-process` |
| **[media-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/media-service)** | `8088` | Photo uploads per event (metadata only, files in object storage) | `media-service-process` |
| **[ecommerce-fluxnova](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/ecommerce-fluxnova)** | `8081` | Order processing sandbox / Fluxnova integration showcase | `ecommerce-sandbox-process` |
| *feed-service* | — | **[Planned]** Activity feed — "Alex RSVP'd", "New event in Hiking Crew" | — |

### Fluxnova & Testing Infrastructure

| Directory / Module | Service Port | Role |
| :--- | :---: | :--- |
| **[fluxnova-webapp-component](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/fluxnova-webapp-component)** | `8080` | Exposes the central Fluxnova Cockpit, task list, and Admin console alongside REST APIs |
| **[fluxnova-cleanup-component](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/fluxnova-cleanup-component)** | *(None)* | Web-less runner using the Fluxnova Java API to periodically purge older deployments and stalled instances |
| **[fluxnova-testing-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/fluxnova-testing-service)** | `8089` | Dedicated load test hub with a dashboard UI to programmatically launch and benchmark workflows |
| **[api-gateway](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/api-gateway)** | `8090` | Single entry point, proxy routing |
| **[auth-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/auth-service)** | `8091` | JWT/OAuth2 token generation and token validation |


---

## Process Engine (BPMN) Matrix

Fluxnova BPM processes are embedded inside the individual microservices:

| Service | Process definition (.bpmn) | Process ID |
|---|---|---|
| **user-service** | `process.bpmn`<br>`user-registration.bpmn` | `user-service-process`<br>`user-registration-process` |
| **group-service** | `group-lifecycle.bpmn` | `group-lifecycle-process` |
| **event-service** | `event-lifecycle.bpmn` | `event-service-process` |
| **rsvp-service** | `process.bpmn` | `rsvp-service-process` |
| **notification-service** | `process.bpmn` | `notification-service-process` |
| **cost-service** | `cost-splitting.bpmn` | `cost-service-process` |
| **media-service** | `media-upload.bpmn` | `media-service-process` |
| **ecommerce-fluxnova** | `process.bpmn` | `ecommerce-sandbox-process` |

---

## Event Flow (Conceptual)

Most interactions across core domain services are asynchronous, typically using message brokers (e.g. Kafka/RabbitMQ).

### RSVP Confirmed
```
User RSVPs "yes"
  → rsvp-service persists it
  → publishes RsvpConfirmed event
      → notification-service sends confirmation to user
      → feed-service (Planned) adds activity entry
      → cost-service updates expected headcount
```

### Event Created / Updated
```
Event created / updated
  → event-service publishes EventCreated / EventUpdated
      → notification-service schedules reminders
      → feed-service (Planned) notifies group members
      → rsvp-service resets or carries over RSVPs (for reschedules)
```

---

## Synchronous gRPC Communications

For high-performance, low-latency synchronous inter-service communication, services use gRPC:

| Client Service | Server Service | RPC Method | Port | Responsibility |
| :--- | :--- | :--- | :---: | :--- |
| **[event-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/event-service)** | **[group-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/group-service)** | `VerifyMembership` | `9082` | Verify that the event creator has membership/permissions in the target group |

---

## Database Strategy


In local development ([docker-compose.yml](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/docker-compose.yml)), databases are hosted containerized.

- **PostgreSQL** (`localhost:5432` / database name `myappdb`): Currently acts as the shared physical database for local configurations, with services owning distinct tables/schemas.
- **Oracle DB** (`localhost:1521` / container name `oracle_db`): Provisioned inside the compose stack, reserved for future service deployments and enterprise engine benchmarks.

| Service | Logical Database/Schema | DB Tech |
|---|---|---|
| **user-service** | `users_db` | PostgreSQL |
| **auth-service** | `users_db` (shares with `user-service`) | PostgreSQL |
| **group-service** | `groups_db` | PostgreSQL |
| **event-service** | `events_db` | PostgreSQL |
| **rsvp-service** | `rsvp_db` | PostgreSQL |
| **cost-service** | `cost_db` | PostgreSQL |
| **notification-service** | `notifications_db` | PostgreSQL |
| **media-service** | `media_db` (metadata only) | PostgreSQL |
| **ecommerce-fluxnova** | `ecommerce_db` | PostgreSQL (Fallback to H2 in-memory) |
| *feed-service* | `feed_db` | **[Planned]** |