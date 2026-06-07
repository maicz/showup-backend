package com.mz.event_service.delegates;

import com.mz.event_service.entities.Event;
import com.mz.event_service.repositories.EventRepository;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("publishPublicEventDelegate")
public class PublishPublicEventDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(PublishPublicEventDelegate.class);

    private final EventRepository eventRepository;

    public PublishPublicEventDelegate(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long eventId = (Long) execution.getVariable("eventId");
        log.info("Publishing public event ID: {}", eventId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setStatus("PUBLISHED");
            eventRepository.save(event);
            log.info("Public event {} marked as PUBLISHED in database", eventId);
        } else {
            log.error("Event not found to publish: {}", eventId);
        }

        execution.setVariable("isPublished", true);
    }
}
