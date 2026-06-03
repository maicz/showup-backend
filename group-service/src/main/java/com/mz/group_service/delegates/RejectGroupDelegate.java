package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("rejectGroupDelegate")
public class RejectGroupDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(RejectGroupDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing RejectGroupDelegate for process: {}", execution.getProcessInstanceId());
    }
}
