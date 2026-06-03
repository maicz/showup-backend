package com.mz.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("auditDelegate")
public class AuditDelegate implements JavaDelegate {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuditDelegate.class);

    // Generic class showcase
    record AuditEntry<T>(String action, T data) {}

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Auditing process steps...");

        String orderId = (String) execution.getVariable("orderId");
        
        // Creating audit entries with different data types
        AuditEntry<String> startAudit = new AuditEntry<>("PROCESS_START", "Order " + orderId);
        AuditEntry<Double> priceAudit = new AuditEntry<>("PRICE_CALCULATED", (Double) execution.getVariable("finalPrice"));
        
        List<AuditEntry<?>> logs = List.of(startAudit, priceAudit);

        // Streams with Generics
        logs.forEach(entry -> log.info("Audit Log: [Action: {}] [Data: {}]", entry.action(), entry.data()));
        
        execution.setVariable("audited", true);
    }
}
