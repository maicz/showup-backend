package com.mz.event_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("publishPublicEventDelegate")
public class PublishPublicEventDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(PublishPublicEventDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Publishing public event: {}", execution.getVariable("eventName"));
        execution.setVariable("isPublished", true);
    }
}
