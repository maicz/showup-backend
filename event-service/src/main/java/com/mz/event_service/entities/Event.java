package com.mz.event_service.entities;

import jakarta.persistence.*;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @Column(length = 255)
    private String venue;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, length = 20)
    private String type; // PUBLIC, PRIVATE

    @Column(nullable = false, length = 20)
    private String status; // PENDING, ACTIVE, PUBLISHED, CANCELLED

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Event(String name, String description, Instant eventDate, String venue, Long creatorId, Long groupId, String type, String status) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.type = type;
        this.status = status;
    }
}
