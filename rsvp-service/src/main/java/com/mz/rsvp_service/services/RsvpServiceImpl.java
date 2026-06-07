package com.mz.rsvp_service.services;

import com.mz.rsvp_service.dto.EventRsvpsSummary;
import com.mz.rsvp_service.dto.RsvpRequest;
import com.mz.rsvp_service.dto.RsvpResponse;
import com.mz.rsvp_service.entities.Rsvp;
import com.mz.rsvp_service.repositories.RsvpRepository;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RsvpServiceImpl implements RsvpService {
    private static final Logger log = LoggerFactory.getLogger(RsvpServiceImpl.class);

    private final RsvpRepository rsvpRepository;
    private final RuntimeService runtimeService;

    @Override
    @Transactional
    public Rsvp createOrUpdateRsvp(RsvpRequest request, Long userId) {
        log.info("Attempting to record RSVP for event {} by user {}", request.getEventId(), userId);

        String statusUpper = request.getStatus().toUpperCase();
        Rsvp rsvp = rsvpRepository.findByEventIdAndUserId(request.getEventId(), userId)
                .orElse(new Rsvp(request.getEventId(), userId, statusUpper));

        rsvp.setStatus(statusUpper);
        Rsvp savedRsvp = rsvpRepository.save(rsvp);
        log.info("RSVP persisted. ID: {}, status: {}", savedRsvp.getId(), savedRsvp.getStatus());

        // Trigger BPM workflow
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("rsvpId", savedRsvp.getId());
            variables.put("eventId", savedRsvp.getEventId());
            variables.put("userId", savedRsvp.getUserId());
            variables.put("status", savedRsvp.getStatus());

            log.info("Starting BPM workflow 'rsvp-service-process' for RSVP ID: {}", savedRsvp.getId());
            runtimeService.startProcessInstanceByKey("rsvp-service-process", variables);
        } catch (Exception e) {
            log.error("Failed to start BPM rsvp-service-process workflow", e);
        }

        return savedRsvp;
    }

    @Override
    @Transactional(readOnly = true)
    public EventRsvpsSummary getEventRsvpsSummary(Long eventId) {
        log.info("Retrieving RSVP summary for event: {}", eventId);
        List<Rsvp> rsvps = rsvpRepository.findByEventId(eventId);
        List<RsvpResponse> responses = rsvps.stream().map(RsvpResponse::new).toList();

        List<RsvpRepository.EventRsvpCount> countsList = rsvpRepository.getRsvpCountsByEventId(eventId);
        Map<String, Long> countsMap = new HashMap<>();
        countsMap.put("YES", 0L);
        countsMap.put("NO", 0L);
        countsMap.put("MAYBE", 0L);

        for (RsvpRepository.EventRsvpCount c : countsList) {
            if (c.getStatus() != null) {
                countsMap.put(c.getStatus().toUpperCase(), c.getCount());
            }
        }

        return new EventRsvpsSummary(eventId, countsMap, responses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rsvp> getUserRsvps(Long userId) {
        log.info("Retrieving RSVPs for user: {}", userId);
        return rsvpRepository.findByUserId(userId);
    }
}
