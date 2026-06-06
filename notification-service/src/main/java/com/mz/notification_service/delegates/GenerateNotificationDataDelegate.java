package com.mz.notification_service.delegates;

import org.json.JSONArray;
import org.json.JSONObject;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("generateNotificationDataDelegate")
public class GenerateNotificationDataDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(GenerateNotificationDataDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Integer counter = (Integer) execution.getVariable("loop_counter");
        if (counter == null) {
            counter = 0;
        }
        counter++;
        execution.setVariable("loop_counter", counter);
        log.info("Generating notification data. Loop counter: {}", counter);

        String auditLogStr = (String) execution.getVariable("audit_log");
        if (auditLogStr == null || auditLogStr.isEmpty()) {
            auditLogStr = "{\"events\":[]}";
        }

        JSONObject auditLog = new JSONObject(auditLogStr);
        JSONArray events = auditLog.optJSONArray("events");
        if (events == null) {
            events = new JSONArray();
            auditLog.put("events", events);
        }

        JSONObject newEvent = new JSONObject();
        newEvent.put("event_type", "DATA_GENERATION");
        newEvent.put("iteration", counter);
        newEvent.put("timestamp", System.currentTimeMillis());
        newEvent.put("message", "Generated data for loop iteration " + counter);
        events.put(newEvent);

        execution.setVariable("audit_log", auditLog.toString());
    }
}
