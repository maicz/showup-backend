package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("manageGroupDelegate")
public class ManageGroupDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ManageGroupDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing ManageGroupDelegate for process: {}", execution.getProcessInstanceId());
    }
}
