package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("archiveOrderDelegate")
public class ArchiveOrderDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ArchiveOrderDelegate.class);
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String orderId = (String) delegateExecution.getVariable("orderId");
        log.info("Archiving order with id: {}", orderId);
        delegateExecution.setVariable("archived", "true");
    }
}
