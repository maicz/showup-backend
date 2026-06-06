package com.mz.group_service.services;

import com.mz.group_service.dto.GroupCreateRequest;
import com.mz.group_service.dto.GroupResponse;
import com.mz.group_service.dto.MembershipRequest;
import com.mz.group_service.entities.Group;
import com.mz.group_service.entities.Membership;

public interface GroupService {
    Group createGroup(GroupCreateRequest request, Long creatorId);
    GroupResponse getGroupDetails(Long groupId);
    Membership addMember(Long groupId, MembershipRequest request, Long actorId);
    void removeMember(Long groupId, Long userId, Long actorId);
    void updateGroupStatus(Long groupId, String status);
    void addMembershipDirect(Long groupId, Long userId, String role);
}
