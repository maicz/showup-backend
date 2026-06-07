package com.mz.group_service.services;

import com.mz.group_service.dto.GroupCreateRequest;
import com.mz.group_service.dto.GroupResponse;
import com.mz.group_service.dto.MembershipRequest;
import com.mz.group_service.entities.Group;
import com.mz.group_service.entities.Membership;
import com.mz.group_service.exceptions.ConflictException;
import com.mz.group_service.exceptions.NotFoundException;
import com.mz.group_service.repositories.GroupRepository;
import com.mz.group_service.repositories.MembershipRepository;
import org.finos.fluxnova.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {
    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final RuntimeService runtimeService;

    @Override
    @Transactional
    public Group createGroup(GroupCreateRequest request, Long creatorId) {
        log.info("Attempting to create group: {} by creator: {}", request.getName(), creatorId);

        if (groupRepository.existsByName(request.getName())) {
            log.warn("Group creation failed. Name already exists: {}", request.getName());
            throw new ConflictException("Group name is already taken");
        }

        // Save Group as PENDING. The BPM process will activate or reject it.
        Group group = new Group(
                request.getName(),
                request.getDescription(),
                creatorId,
                "PENDING",
                request.getTier() != null ? request.getTier().toUpperCase() : "STANDARD"
        );
        Group savedGroup = groupRepository.save(group);
        log.info("Group persisted in PENDING state. ID: {}", savedGroup.getId());

        // Trigger BPM group lifecycle process
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("groupId", savedGroup.getId());
            variables.put("groupName", savedGroup.getName());
            variables.put("creatorId", savedGroup.getCreatorId());
            variables.put("groupTier", savedGroup.getTier());
            variables.put("description", savedGroup.getDescription());

            log.info("Starting BPM workflow 'group-lifecycle-process' for group: {}", savedGroup.getName());
            runtimeService.startProcessInstanceByKey("group-lifecycle-process", variables);
        } catch (Exception e) {
            log.error("Failed to start BPM group-lifecycle-process workflow", e);
        }

        return savedGroup;
    }

    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupDetails(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));
        
        List<Membership> memberships = membershipRepository.findByGroupId(groupId);
        return new GroupResponse(group, memberships);
    }

    @Override
    @Transactional
    public Membership addMember(Long groupId, MembershipRequest request, Long actorId) {
        log.info("Actor {} attempting to add user {} to group {}", actorId, request.getUserId(), groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));

        if (!"ACTIVE".equals(group.getStatus())) {
            throw new IllegalArgumentException("Cannot add members to a group that is not ACTIVE");
        }

        // Check if actor has admin rights to add a member (unless it's self-joining, but let's require admin or creator check)
        Membership actorMembership = membershipRepository.findByGroupIdAndUserId(groupId, actorId)
                .orElseThrow(() -> new IllegalArgumentException("Actor is not a member of this group"));

        if (!"ADMIN".equals(actorMembership.getRole()) && !group.getCreatorId().equals(actorId)) {
            throw new IllegalArgumentException("Only group administrators can add new members");
        }

        if (membershipRepository.existsByGroupIdAndUserId(groupId, request.getUserId())) {
            throw new ConflictException("User is already a member of this group");
        }

        Membership membership = new Membership(groupId, request.getUserId(), request.getRole());
        Membership savedMembership = membershipRepository.save(membership);
        log.info("User {} added to group {} successfully", request.getUserId(), groupId);
        
        return savedMembership;
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long userId, Long actorId) {
        log.info("Actor {} attempting to remove user {} from group {}", actorId, userId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));

        // Check permissions: either actor is removing themselves, or actor is ADMIN / creator
        if (!userId.equals(actorId)) {
            Membership actorMembership = membershipRepository.findByGroupIdAndUserId(groupId, actorId)
                    .orElseThrow(() -> new IllegalArgumentException("Actor is not a member of this group"));

            if (!"ADMIN".equals(actorMembership.getRole()) && !group.getCreatorId().equals(actorId)) {
                throw new IllegalArgumentException("Only group administrators can remove members");
            }
        }

        if (!membershipRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new NotFoundException("Membership not found");
        }

        // Cannot remove the creator/primary admin unless group is deleted
        if (group.getCreatorId().equals(userId)) {
            throw new IllegalArgumentException("Cannot remove the group creator from the group");
        }

        membershipRepository.deleteByGroupIdAndUserId(groupId, userId);
        log.info("User {} removed from group {} successfully", userId, groupId);
    }

    @Override
    @Transactional
    public void updateGroupStatus(Long groupId, String status) {
        log.info("Updating group {} status to: {}", groupId, status);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found with id " + groupId));
        group.setStatus(status);
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void addMembershipDirect(Long groupId, Long userId, String role) {
        log.info("Directly adding membership: group={}, user={}, role={}", groupId, userId, role);
        if (!membershipRepository.existsByGroupIdAndUserId(groupId, userId)) {
            Membership membership = new Membership(groupId, userId, role);
            membershipRepository.save(membership);
        }
    }
}
