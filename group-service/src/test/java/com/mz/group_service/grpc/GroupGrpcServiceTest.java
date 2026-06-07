package com.mz.group_service.grpc;

import com.mz.group_service.entities.Membership;
import com.mz.group_service.repositories.MembershipRepository;
import com.mz.grpc.MembershipRequest;
import com.mz.grpc.MembershipResponse;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class GroupGrpcServiceTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testVerifyMembershipSuccess() {
        MembershipRepository repository = mock(MembershipRepository.class);
        Membership membership = new Membership(1L, 100L, "ADMIN");
        when(repository.findByGroupIdAndUserId(1L, 100L)).thenReturn(Optional.of(membership));

        GroupGrpcServiceImpl service = new GroupGrpcServiceImpl(repository);

        StreamObserver<MembershipResponse> responseObserver = mock(StreamObserver.class);
        MembershipRequest request = MembershipRequest.newBuilder()
                .setGroupId(1L)
                .setUserId(100L)
                .build();

        service.verifyMembership(request, responseObserver);

        ArgumentCaptor<MembershipResponse> responseCaptor = ArgumentCaptor.forClass(MembershipResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        MembershipResponse response = responseCaptor.getValue();
        assertTrue(response.getIsMember());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testVerifyMembershipNotFound() {
        MembershipRepository repository = mock(MembershipRepository.class);
        when(repository.findByGroupIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        GroupGrpcServiceImpl service = new GroupGrpcServiceImpl(repository);

        StreamObserver<MembershipResponse> responseObserver = mock(StreamObserver.class);
        MembershipRequest request = MembershipRequest.newBuilder()
                .setGroupId(1L)
                .setUserId(100L)
                .build();

        service.verifyMembership(request, responseObserver);

        ArgumentCaptor<MembershipResponse> responseCaptor = ArgumentCaptor.forClass(MembershipResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        MembershipResponse response = responseCaptor.getValue();
        assertFalse(response.getIsMember());
        assertEquals("NONE", response.getRole());
    }
}
