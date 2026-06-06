package com.mz.notification_service;

import org.finos.fluxnova.bpm.engine.HistoryService;
import org.finos.fluxnova.bpm.engine.ManagementService;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.finos.fluxnova.bpm.engine.history.HistoricProcessInstance;
import org.finos.fluxnova.bpm.engine.history.HistoricVariableInstance;
import org.finos.fluxnova.bpm.engine.runtime.Job;
import org.finos.fluxnova.bpm.engine.runtime.ProcessInstance;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationProcessTest {
    private static final Logger log = LoggerFactory.getLogger(NotificationProcessTest.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @Test
    public void testNotificationMainProcessFlow() throws Exception {
        log.info("Starting notification-main-process instance");
        
        // Start the process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("notification-main-process");
        assertNotNull(processInstance);
        log.info("Process instance started: {}", processInstance.getId());

        // Execute asynchronous jobs synchronously in a loop until none are left
        executeAvailableJobs();

        // Assert process has finished successfully
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();
        
        assertNotNull(historicProcessInstance);
        assertEquals("COMPLETED", historicProcessInstance.getState());

        // Fetch final audit_log variable from history
        HistoricVariableInstance auditLogVar = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("audit_log")
                .singleResult();

        assertNotNull(auditLogVar);
        String auditLogStr = (String) auditLogVar.getValue();
        assertNotNull(auditLogStr);
        log.info("Final audit_log JSON: {}", auditLogStr);

        // Verify JSON contents
        JSONObject auditLog = new JSONObject(auditLogStr);
        assertTrue(auditLog.has("events"));
        
        // Check that GenerateNotificationDataDelegate ran 3 times
        int dataGenCount = 0;
        int subProcessMergeCount = 0;
        
        for (int i = 0; i < auditLog.getJSONArray("events").length(); i++) {
            JSONObject event = auditLog.getJSONArray("events").getJSONObject(i);
            String eventType = event.getString("event_type");
            if ("DATA_GENERATION".equals(eventType)) {
                dataGenCount++;
            } else if ("SUB_PROCESS_MERGE".equals(eventType)) {
                subProcessMergeCount++;
            }
        }
        
        assertEquals(3, dataGenCount, "GenerateNotificationDataDelegate should have executed exactly 3 times in the loop");
        assertEquals(3, subProcessMergeCount, "Sub-process should have executed and merged its log 3 times (once per iteration)");

        // Verify sub process output was merged correctly
        for (int i = 0; i < auditLog.getJSONArray("events").length(); i++) {
            JSONObject event = auditLog.getJSONArray("events").getJSONObject(i);
            if ("SUB_PROCESS_MERGE".equals(event.getString("event_type"))) {
                JSONObject subDetails = event.getJSONObject("sub_process_details");
                assertTrue(subDetails.has("sub_events"));
                assertTrue(subDetails.getJSONArray("sub_events").length() >= 5);
            }
        }
    }

    private void executeAvailableJobs() {
        int maxIterations = 100;
        int iterations = 0;
        List<Job> jobs;
        
        while (iterations < maxIterations && !(jobs = managementService.createJobQuery().list()).isEmpty()) {
            for (Job job : jobs) {
                try {
                    log.info("Executing job: {} for process definition: {}", job.getId(), job.getProcessDefinitionKey());
                    managementService.executeJob(job.getId());
                } catch (Exception e) {
                    log.error("Failed to execute job {}", job.getId(), e);
                }
            }
            iterations++;
        }
    }
}
