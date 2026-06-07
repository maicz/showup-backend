package com.mz.rsvp_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("recordRsvpDelegate")
public class RecordRsvpDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(RecordRsvpDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long rsvpId = (Long) execution.getVariable("rsvpId");
        Long eventId = (Long) execution.getVariable("eventId");
        Long userId = (Long) execution.getVariable("userId");
        String status = (String) execution.getVariable("status");

        log.info("BPMN: RecordRsvpDelegate successfully processed RSVP. ID: {}, Event: {}, User: {}, Status: {}", 
                rsvpId, eventId, userId, status);
        
        execution.setVariable("rsvpRecorded", true);
    }
}
