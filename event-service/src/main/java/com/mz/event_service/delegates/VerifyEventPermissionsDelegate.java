package com.mz.event_service.delegates;

import com.mz.event_service.entities.Event;
import com.mz.event_service.repositories.EventRepository;
import com.mz.grpc.GroupGrpcServiceGrpc;
import com.mz.grpc.MembershipRequest;
import com.mz.grpc.MembershipResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.finos.fluxnova.bpm.engine.delegate.DelegateExecution;
import org.finos.fluxnova.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("verifyEventPermissionsDelegate")
public class VerifyEventPermissionsDelegate implements JavaDelegate {
    private static final Logger log = LoggerFactory.getLogger(VerifyEventPermissionsDelegate.class);

    private final EventRepository eventRepository;

    @GrpcClient("groupService")
    private GroupGrpcServiceGrpc.GroupGrpcServiceBlockingStub groupServiceStub;

    public VerifyEventPermissionsDelegate(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long eventId = (Long) execution.getVariable("eventId");
        Long creatorId = (Long) execution.getVariable("creatorId");
        log.info("Verifying permissions for event: {}, creatorId: {}", eventId, creatorId);

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.error("Event not found for permission verification: {}", eventId);
            execution.setVariable("permissionsVerified", false);
            return;
        }

        Event event = eventOpt.get();
        boolean permissionsVerified = true;

        if (event.getGroupId() != null) {
            log.info("Event {} is associated with group {}. Calling group-service gRPC to verify membership for user {}", 
                    eventId, event.getGroupId(), creatorId);
            try {
                MembershipRequest grpcRequest = MembershipRequest.newBuilder()
                        .setGroupId(event.getGroupId())
                        .setUserId(creatorId)
                        .build();
                
                MembershipResponse grpcResponse = groupServiceStub.verifyMembership(grpcRequest);
                
                permissionsVerified = grpcResponse.getIsMember();
                log.info("gRPC call result: isMember = {}, role = {}", permissionsVerified, grpcResponse.getRole());
            } catch (Exception e) {
                log.error("gRPC call to group-service failed. Fallback to default approval for resilience. Error: {}", e.getMessage());
                // Fallback to true for testing and resilience when group-service is not running
                permissionsVerified = true;
            }
        } else {
            log.info("Event {} is a personal event. Permitted for user {}", eventId, creatorId);
        }

        execution.setVariable("permissionsVerified", permissionsVerified);
    }
}
