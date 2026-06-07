package com.mz.group_service.controllers;

import com.mz.group_service.dto.GroupCreateRequest;
import com.mz.group_service.dto.GroupResponse;
import com.mz.group_service.dto.MembershipRequest;
import com.mz.group_service.entities.Group;
import com.mz.group_service.entities.Membership;
import com.mz.group_service.security.UserPrincipal;
import com.mz.group_service.services.GroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(
            @Valid @RequestBody GroupCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        log.info("REST request to create group: {} by user: {}", request.getName(), principal.getUsername());
        Group group = groupService.createGroup(request, principal.getId());
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupDetails(@PathVariable("id") Long id) {
        log.info("REST request to get details for group: {}", id);
        GroupResponse response = groupService.getGroupDetails(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<Membership> addMember(
            @PathVariable("id") Long id,
            @Valid @RequestBody MembershipRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        log.info("REST request to add user {} to group {} by actor {}", request.getUserId(), id, principal.getUsername());
        Membership membership = groupService.addMember(id, request, principal.getId());
        return new ResponseEntity<>(membership, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable("id") Long id,
            @PathVariable("userId") Long userId,
            @AuthenticationPrincipal UserPrincipal principal) {
        
        log.info("REST request to remove user {} from group {} by actor {}", userId, id, principal.getUsername());
        groupService.removeMember(id, userId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
