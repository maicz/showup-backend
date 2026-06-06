package com.mz.notification_service.delegates;

import org.json.JSONArray;
import org.json.JSONObject;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("subInitDelegate")
public class SubInitDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(SubInitDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("Initializing sub-process audit log");
        JSONObject auditLog = new JSONObject();
        JSONArray events = new JSONArray();
        
        JSONObject initEvent = new JSONObject();
        initEvent.put("activity", "SUB_INIT");
        initEvent.put("status", "SUCCESS");
        initEvent.put("timestamp", System.currentTimeMillis());
        events.put(initEvent);
        
        auditLog.put("sub_events", events);
        execution.setVariable("audit_log", auditLog.toString());
    }
}
