package com.mz.event_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("processEventDelegate")
public class ProcessEventDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ProcessEventDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing ProcessEventDelegate for process: {}", execution.getProcessInstanceId());
    }
}
