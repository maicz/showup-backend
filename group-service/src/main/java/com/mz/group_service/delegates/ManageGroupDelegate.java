package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("manageGroupDelegate")
public class ManageGroupDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ManageGroupDelegate.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String groupName = (String) execution.getVariable("groupName");
        String description = (String) execution.getVariable("description");
        String groupTier = (String) execution.getVariable("groupTier");
        log.info("Executing ManageGroupDelegate for group name: {}", groupName);

        boolean isApproved = true;
        if (description != null && (description.toLowerCase().contains("spam") || description.toLowerCase().contains("rejected"))) {
            log.warn("Group description contains forbidden content, rejecting group creation");
            isApproved = false;
        }

        if (groupTier == null) {
            groupTier = "STANDARD";
        }

        execution.setVariable("isApproved", isApproved);
        execution.setVariable("groupTier", groupTier);
        log.info("Approval assessment complete. isApproved={}, groupTier={}", isApproved, groupTier);
    }
}
