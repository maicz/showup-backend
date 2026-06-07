package com.mz.event_service.controllers;

import com.mz.event_service.dto.EventCreateRequest;
import com.mz.event_service.dto.EventResponse;
import com.mz.event_service.entities.Event;
import com.mz.event_service.security.UserPrincipal;
import com.mz.event_service.services.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        log.info("REST request to create event: {} by user: {}", request.getName(), principal.getUsername());
        Event event = eventService.createEvent(request, principal.getId());
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventDetails(@PathVariable("id") Long id) {
        log.info("REST request to get details for event: {}", id);
        EventResponse response = eventService.getEventDetails(id);
        return ResponseEntity.ok(response);
    }
}
