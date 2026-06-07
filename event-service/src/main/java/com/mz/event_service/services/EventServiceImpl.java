package com.mz.event_service.services;

import com.mz.event_service.dto.EventCreateRequest;
import com.mz.event_service.dto.EventResponse;
import com.mz.event_service.entities.Event;
import com.mz.event_service.exceptions.NotFoundException;
import com.mz.event_service.repositories.EventRepository;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final RuntimeService runtimeService;

    @Override
    @Transactional
    public Event createEvent(EventCreateRequest request, Long creatorId) {
        log.info("Attempting to create event: {} by creator: {}", request.getName(), creatorId);

        // Save Event as PENDING. The BPM process will activate/publish or reject it.
        Event event = new Event(
                request.getName(),
                request.getDescription(),
                request.getEventDate(),
                request.getVenue(),
                creatorId,
                request.getGroupId(),
                request.getType() != null ? request.getType().toUpperCase() : "PUBLIC",
                "PENDING"
        );
        Event savedEvent = eventRepository.save(event);
        log.info("Event persisted in PENDING state. ID: {}", savedEvent.getId());

        // Trigger BPM event lifecycle process
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("eventId", savedEvent.getId());
            variables.put("eventName", savedEvent.getName());
            variables.put("creatorId", savedEvent.getCreatorId());
            variables.put("eventType", savedEvent.getType());
            variables.put("description", savedEvent.getDescription());

            log.info("Starting BPM workflow 'event-service-process' for event: {}", savedEvent.getName());
            runtimeService.startProcessInstanceByKey("event-service-process", variables);
        } catch (Exception e) {
            log.error("Failed to start BPM event-service-process workflow", e);
        }

        return savedEvent;
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventDetails(Long id) {
        log.info("Retrieving details for event: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id " + id));
        return new EventResponse(event);
    }

    @Override
    @Transactional
    public void updateEventStatus(Long id, String status) {
        log.info("Updating event {} status to: {}", id, status);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id " + id));
        event.setStatus(status);
        eventRepository.save(event);
    }
}
