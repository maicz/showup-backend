package com.mz.event_service.dto;

import com.mz.event_service.entities.Event;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private Instant eventDate;
    private String venue;
    private Long creatorId;
    private Long groupId;
    private String type;
    private String status;
    private Instant createdAt;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.eventDate = event.getEventDate();
        this.venue = event.getVenue();
        this.creatorId = event.getCreatorId();
        this.groupId = event.getGroupId();
        this.type = event.getType();
        this.status = event.getStatus();
        this.createdAt = event.getCreatedAt();
    }
}
