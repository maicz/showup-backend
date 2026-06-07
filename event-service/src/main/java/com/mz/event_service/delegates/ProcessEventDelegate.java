package com.mz.event_service.delegates;

import com.mz.event_service.entities.Event;
import com.mz.event_service.repositories.EventRepository;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Component("processEventDelegate")
@RequiredArgsConstructor
public class ProcessEventDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ProcessEventDelegate.class);

    private final EventRepository eventRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long eventId = (Long) execution.getVariable("eventId");
        log.info("Executing ProcessEventDelegate for eventId: {}", eventId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.error("Event not found with ID: {}", eventId);
            execution.setVariable("isValid", false);
            return;
        }

        Event event = eventOpt.get();
        boolean isValid = true;

        // Validation 1: Name must be between 3 and 100 characters
        if (event.getName() == null || event.getName().trim().length() < 3 || event.getName().length() > 100) {
            log.warn("Validation failed: event name '{}' is invalid", event.getName());
            isValid = false;
        }

        // Validation 2: Event date must be in the future
        if (event.getEventDate() == null || event.getEventDate().isBefore(Instant.now())) {
            log.warn("Validation failed: event date '{}' is in the past", event.getEventDate());
            isValid = false;
        }

        if (!isValid) {
            event.setStatus("INVALID");
            eventRepository.save(event);
            log.info("Event {} marked as INVALID in database", eventId);
        }

        execution.setVariable("isValid", isValid);
        log.info("Event validation result for ID {}: {}", eventId, isValid);
    }
}
