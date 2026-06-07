package com.mz.group_service.grpc;

import com.mz.group_service.entities.Membership;
import com.mz.group_service.repositories.MembershipRepository;
import com.mz.grpc.GroupGrpcServiceGrpc;
import com.mz.grpc.MembershipRequest;
import com.mz.grpc.MembershipResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@GrpcService
public class GroupGrpcServiceImpl extends GroupGrpcServiceGrpc.GroupGrpcServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(GroupGrpcServiceImpl.class);

    private final MembershipRepository membershipRepository;

    public GroupGrpcServiceImpl(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public void verifyMembership(MembershipRequest request, StreamObserver<MembershipResponse> responseObserver) {
        long groupId = request.getGroupId();
        long userId = request.getUserId();
        log.info("gRPC: Received VerifyMembership request for groupId: {}, userId: {}", groupId, userId);

        Optional<Membership> membershipOpt = membershipRepository.findByGroupIdAndUserId(groupId, userId);
        
        MembershipResponse response;
        if (membershipOpt.isPresent()) {
            Membership membership = membershipOpt.get();
            response = MembershipResponse.newBuilder()
                    .setIsMember(true)
                    .setRole(membership.getRole() != null ? membership.getRole() : "MEMBER")
                    .build();
            log.info("gRPC: User {} IS a member of group {} with role {}", userId, groupId, membership.getRole());
        } else {
            response = MembershipResponse.newBuilder()
                    .setIsMember(false)
                    .setRole("NONE")
                    .build();
            log.info("gRPC: User {} IS NOT a member of group {}", userId, groupId);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
