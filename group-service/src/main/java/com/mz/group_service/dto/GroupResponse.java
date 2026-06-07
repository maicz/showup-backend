package com.mz.group_service.dto;

import com.mz.group_service.entities.Group;
import com.mz.group_service.entities.Membership;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private String status;
    private String tier;
    private Instant createdAt;
    private List<Membership> memberships;

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
}
