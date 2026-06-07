package com.mz.event_service.delegates;

import com.mz.event_service.entities.Event;
import com.mz.event_service.repositories.EventRepository;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("setupPrivateAccessDelegate")
public class SetupPrivateAccessDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SetupPrivateAccessDelegate.class);

    private final EventRepository eventRepository;

    public SetupPrivateAccessDelegate(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long eventId = (Long) execution.getVariable("eventId");
        log.info("Setting up private access for event ID: {}", eventId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setStatus("ACTIVE");
            eventRepository.save(event);
            log.info("Private event {} marked as ACTIVE in database", eventId);
        } else {
            log.error("Event not found to configure private access: {}", eventId);
        }

        execution.setVariable("accessConfigured", "PRIVATE_ONLY");
    }
}
