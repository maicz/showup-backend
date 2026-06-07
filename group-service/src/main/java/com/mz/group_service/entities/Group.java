package com.mz.group_service.entities;

import jakarta.persistence.*;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, ACTIVE, REJECTED

    @Column(nullable = false, length = 20)
    private String tier; // STANDARD, PREMIUM

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Group(String name, String description, Long creatorId, String status, String tier) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.status = status;
        this.tier = tier;
    }
}
