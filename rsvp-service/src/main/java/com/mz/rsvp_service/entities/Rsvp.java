package com.mz.rsvp_service.entities;

import jakarta.persistence.*;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rsvps", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Rsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String status; // YES, NO, MAYBE

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Rsvp(Long eventId, Long userId, String status) {
        this.eventId = eventId;
        this.userId = userId;
        this.status = status;
    }
}
