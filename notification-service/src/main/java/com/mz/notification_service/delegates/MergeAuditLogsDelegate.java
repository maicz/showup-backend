package com.mz.notification_service.delegates;

import org.json.JSONArray;
import org.json.JSONObject;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("mergeAuditLogsDelegate")
public class MergeAuditLogsDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(MergeAuditLogsDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Merging comm_audit_log into main audit_log");

        String auditLogStr = (String) execution.getVariable("audit_log");
        String commAuditLogStr = (String) execution.getVariable("comm_audit_log");

        if (auditLogStr == null || auditLogStr.isEmpty()) {
            auditLogStr = "{\"events\":[]}";
        }

        JSONObject mainAuditLog = new JSONObject(auditLogStr);
        JSONArray events = mainAuditLog.optJSONArray("events");
        if (events == null) {
            events = new JSONArray();
            mainAuditLog.put("events", events);
        }

        if (commAuditLogStr != null && !commAuditLogStr.isEmpty()) {
            log.info("Found communication audit log to merge: {}", commAuditLogStr);
            JSONObject subAuditLog = new JSONObject(commAuditLogStr);
            
            JSONObject mergeEvent = new JSONObject();
            mergeEvent.put("event_type", "SUB_PROCESS_MERGE");
            mergeEvent.put("timestamp", System.currentTimeMillis());
            mergeEvent.put("sub_process_details", subAuditLog);
            
            events.put(mergeEvent);
        } else {
            log.warn("No comm_audit_log found to merge");
            JSONObject warningEvent = new JSONObject();
            warningEvent.put("event_type", "SUB_PROCESS_MERGE_WARNING");
            warningEvent.put("timestamp", System.currentTimeMillis());
            warningEvent.put("message", "No comm_audit_log output received from sub-process");
            events.put(warningEvent);
        }

        String updatedAuditLog = mainAuditLog.toString();
        execution.setVariable("audit_log", updatedAuditLog);
        log.info("Merged audit log: {}", updatedAuditLog);
    }
}
