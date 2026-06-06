package com.mz.notification_service.delegates;

import org.json.JSONArray;
import org.json.JSONObject;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.Random;

@Component("generateAuditEntryDelegate")
public class GenerateAuditEntryDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(GenerateAuditEntryDelegate.class);
    private final Random random = new Random();

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Generating random test audit entry inside sub-process");
        String auditLogStr = (String) execution.getVariable("audit_log");
        if (auditLogStr == null || auditLogStr.isEmpty()) {
            auditLogStr = "{\"sub_events\":[]}";
        }

        JSONObject auditLog = new JSONObject(auditLogStr);
        JSONArray events = auditLog.optJSONArray("sub_events");
        if (events == null) {
            events = new JSONArray();
            auditLog.put("sub_events", events);
        }

        JSONObject randomEntry = new JSONObject();
        randomEntry.put("activity", "GENERATE_RANDOM_AUDIT_ENTRY");
        randomEntry.put("uuid", UUID.randomUUID().toString());
        randomEntry.put("test_metric_ms", 10 + random.nextInt(190));
        randomEntry.put("random_success", random.nextBoolean());
        randomEntry.put("timestamp", System.currentTimeMillis());
        events.put(randomEntry);

        String updatedAuditLog = auditLog.toString();
        execution.setVariable("audit_log", updatedAuditLog);
        log.info("Sub-process audit_log updated: {}", updatedAuditLog);
    }
}
