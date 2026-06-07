package com.mz.rsvp_service.services;

import com.mz.rsvp_service.dto.EventRsvpsSummary;
import com.mz.rsvp_service.dto.RsvpRequest;
import com.mz.rsvp_service.entities.Rsvp;

import java.util.List;

public interface RsvpService {
    Rsvp createOrUpdateRsvp(RsvpRequest request, Long userId);
    EventRsvpsSummary getEventRsvpsSummary(Long eventId);
    List<Rsvp> getUserRsvps(Long userId);
}
