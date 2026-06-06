package com.mz.group_service.dto;

import com.mz.group_service.entities.Group;
import com.mz.group_service.entities.Membership;

import java.time.Instant;
import java.util.List;

public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private String status;
    private String tier;
    private Instant createdAt;
    private List<Membership> memberships;

    public GroupResponse() {}

    public GroupResponse(Group group, List<Membership> memberships) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.creatorId = group.getCreatorId();
        this.status = group.getStatus();
        this.tier = group.getTier();
        this.createdAt = group.getCreatedAt();
        this.memberships = memberships;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<Membership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<Membership> memberships) {
        this.memberships = memberships;
    }
}
