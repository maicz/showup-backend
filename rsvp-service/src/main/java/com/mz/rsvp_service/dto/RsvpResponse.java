package com.mz.rsvp_service.dto;

import com.mz.rsvp_service.entities.Rsvp;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RsvpResponse {
    private Long id;
    private Long eventId;
    private Long userId;
    private String status;
    private Instant createdAt;

    public RsvpResponse(Rsvp rsvp) {
        this.id = rsvp.getId();
        this.eventId = rsvp.getEventId();
        this.userId = rsvp.getUserId();
        this.status = rsvp.getStatus();
        this.createdAt = rsvp.getCreatedAt();
    }
}
