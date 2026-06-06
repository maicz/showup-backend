package com.mz.notification_service.delegates;

import org.json.JSONArray;
import org.json.JSONObject;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("sendCommunicationDelegate")
public class SendCommunicationDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SendCommunicationDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Sending communication to recipient");
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
        
        JSONObject newEvent = new JSONObject();
        newEvent.put("activity", "SEND_COMMUNICATION");
        newEvent.put("status", "DISPATCHED");
        newEvent.put("timestamp", System.currentTimeMillis());
        events.put(newEvent);

        execution.setVariable("audit_log", auditLog.toString());
    }
}
