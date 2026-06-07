package com.mz.group_service.delegates;

import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("rejectGroupDelegate")
@RequiredArgsConstructor
public class RejectGroupDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(RejectGroupDelegate.class);

    private final com.mz.group_service.services.GroupService groupService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long groupId = (Long) execution.getVariable("groupId");
        log.warn("Rejecting group creation: ID={}", groupId);

        groupService.updateGroupStatus(groupId, "REJECTED");
        
        log.warn("Group {} marked as REJECTED in database", groupId);
    }
}
