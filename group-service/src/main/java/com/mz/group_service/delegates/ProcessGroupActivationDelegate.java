package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("processGroupActivationDelegate")
public class ProcessGroupActivationDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(ProcessGroupActivationDelegate.class);

    private final com.mz.group_service.services.GroupService groupService;

    public ProcessGroupActivationDelegate(com.mz.group_service.services.GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long groupId = (Long) execution.getVariable("groupId");
        Long creatorId = (Long) execution.getVariable("creatorId");
        log.info("Activating group: ID={}, CreatorID={}", groupId, creatorId);

        groupService.updateGroupStatus(groupId, "ACTIVE");
        groupService.addMembershipDirect(groupId, creatorId, "ADMIN");
        
        log.info("Group {} activated successfully. Creator {} joined as ADMIN", groupId, creatorId);
    }
}
