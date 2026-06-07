package com.mz.rsvp_service.controllers;

import com.mz.rsvp_service.dto.EventRsvpsSummary;
import com.mz.rsvp_service.dto.RsvpRequest;
import com.mz.rsvp_service.entities.Rsvp;
import com.mz.rsvp_service.security.UserPrincipal;
import com.mz.rsvp_service.services.RsvpService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rsvps")
@RequiredArgsConstructor
public class RsvpController {
    private static final Logger log = LoggerFactory.getLogger(RsvpController.class);

    private final RsvpService rsvpService;

    @PostMapping
    public ResponseEntity<Rsvp> createOrUpdateRsvp(
            @Valid @RequestBody RsvpRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        log.info("REST request to record RSVP: event {} status {} by user {}", 
                request.getEventId(), request.getStatus(), principal.getUsername());
        Rsvp rsvp = rsvpService.createOrUpdateRsvp(request, principal.getId());
        return new ResponseEntity<>(rsvp, HttpStatus.CREATED);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventRsvpsSummary> getEventRsvpsSummary(@PathVariable("eventId") Long eventId) {
        log.info("REST request to get RSVP summary for event: {}", eventId);
        EventRsvpsSummary summary = rsvpService.getEventRsvpsSummary(eventId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Rsvp>> getMyRsvps(@AuthenticationPrincipal UserPrincipal principal) {
        log.info("REST request to get RSVPs for current user: {}", principal.getUsername());
        List<Rsvp> myRsvps = rsvpService.getUserRsvps(principal.getId());
        return ResponseEntity.ok(myRsvps);
    }
}
