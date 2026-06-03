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
        log.info("Executing RecordRsvpDelegate for process: {}", execution.getProcessInstanceId());
    }
}
