package com.mz.notification_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("initAuditLogDelegate")
public class InitAuditLogDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(InitAuditLogDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Initializing audit_log and loop_counter for process {}", execution.getProcessInstanceId());
        execution.setVariable("loop_counter", 0);
        execution.setVariable("audit_log", "{\"events\":[]}");
    }
}
