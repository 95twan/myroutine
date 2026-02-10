package com.node5.shopservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberClient {
    @DeleteMapping("/internal/members/{memberId}/roles/{role}")
    ResponseEntity<Void> deleteMemberRole(
            @PathVariable UUID memberId,
            @PathVariable String role
    );
}
