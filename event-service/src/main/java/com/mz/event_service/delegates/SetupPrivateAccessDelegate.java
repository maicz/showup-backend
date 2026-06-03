package com.mz.event_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("setupPrivateAccessDelegate")
public class SetupPrivateAccessDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SetupPrivateAccessDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Setting up private access for event: {}", execution.getVariable("eventName"));
        execution.setVariable("accessConfigured", "PRIVATE_ONLY");
    }
}
