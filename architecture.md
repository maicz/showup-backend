# Showup — Backend Architecture Outline

## Services

### Core Domain Services

| Service | Responsibility |
|---|---|
| **user-service** | Registration, login, profiles, friendships/contacts |
| **group-service** | Groups (e.g. "Hiking Crew"), membership, roles (admin/member) |
| **event-service** | Create/edit events, scheduling, venue, recurrence rules |
| **rsvp-service** | Attendance responses, headcount, waitlists |
| **notification-service** | Email/push/in-app reminders, digest scheduling |

### Supporting Services

| Service | Responsibility |
|---|---|
| **cost-service** | Per-event expenses, splitting logic, settlement tracking |
| **media-service** | Photo uploads per event (S3/GCS-backed), albums |
| **feed-service** | Activity feed — "Alex RSVP'd", "New event in Hiking Crew" |
### Infrastructure

| Component | Role |
|---|---|
| **api-gateway** | Single entry point, auth token validation, routing |
| **auth-service** | JWT/OAuth2 issuing (custom Spring Authorization Server) |
| **fluxnova-testing-service** | Load test hub for programmatically triggering workflow test cases |

---

## Event Flow

Most interactions are async over Kafka/RabbitMQ.

### RSVP Confirmed
```
User RSVPs "yes"
  → rsvp-service persists it
  → publishes RsvpConfirmed event
      → notification-service sends confirmation to user
      → feed-service adds activity entry
      → cost-service updates expected headcount
```

### Event Created / Updated
```
Event created / updated
  → event-service publishes EventCreated / EventUpdated
      → notification-service schedules reminders
      → feed-service notifies group members
      → rsvp-service resets or carries over RSVPs (for reschedules)
```

### Reminder Day-Of
```
Event day -24h
  → notification-service publishes ReminderDue (scheduled job)
      → sends push/email to all pending RSVPs
```

---

## Suggested Topic / Queue Names

```
showup.events          # EventCreated, EventUpdated, EventCancelled
showup.rsvps           # RsvpConfirmed, RsvpDeclined, RsvpChanged
showup.notifications   # ReminderDue, DigestReady
showup.media           # PhotoUploaded
showup.costs           # ExpenseAdded, SettlementRequested
```

---

## Database Strategy

Each service owns its own PostgreSQL schema or database — no cross-service joins.

| Service | Database |
|---|---|
| user-service | `users_db` |
| group-service | `groups_db` |
| event-service | `events_db` |
| rsvp-service | `rsvp_db` |
| cost-service | `cost_db` |
| notification-service | `notifications_db` |
| media-service | `media_db` (metadata only, files in object storage) |
| feed-service | `feed_db` |

---

## Build Order

A pragmatic sequence to avoid getting stuck:

1. `user-service` + `auth-service` — nothing works without identity
2. `group-service` + `event-service` — the core domain
3. `rsvp-service` — makes it interactive
4. `notification-service` — makes it actually useful
5. `cost-service`, `media-service`, `feed-service` — enrichment layer

---

## Fluxnova

Fluxnova BPM is used in various services to model business processes and run state machines.
- **[fluxnova-webapp-component](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/fluxnova-webapp-component)**: Exposes the central Fluxnova Cockpit, task list, and Admin console alongside REST APIs.
- **[fluxnova-cleanup-component](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/fluxnova-cleanup-component)**: Automatically runs history cleanups to purge older deployments and stalled instances.
- **[fluxnova-testing-service](file:///Users/mihaiz/dev/projects/showup-app/showup-backend/fluxnova-testing-service)**: Dedicated load testing helper with an interactive dashboard UI for programmatically launching process instances.