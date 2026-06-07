package com.mz.event_service.services;

import com.mz.event_service.dto.EventCreateRequest;
import com.mz.event_service.dto.EventResponse;
import com.mz.event_service.entities.Event;

public interface EventService {
    Event createEvent(EventCreateRequest request, Long creatorId);
    EventResponse getEventDetails(Long id);
    void updateEventStatus(Long id, String status);
}
