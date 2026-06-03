package com.mz.user_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("manageUserDelegate")
public class ManageUserDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ManageUserDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing ManageUserDelegate for process: {}", execution.getProcessInstanceId());
    }
}
