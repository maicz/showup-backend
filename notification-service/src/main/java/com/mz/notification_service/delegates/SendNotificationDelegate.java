package com.mz.notification_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sendNotificationDelegate")
public class SendNotificationDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SendNotificationDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Executing SendNotificationDelegate for process: {}", execution.getProcessInstanceId());
    }
}
