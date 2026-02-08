package com.node5.shopservice.client;

import com.node5.shopservice.client.dto.RoleModifyRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "member-service")
public interface MemberClient {
    @PostMapping("/internal/members/{memberId}/roles")
    ResponseEntity<Void> addMemberRole(
            @PathVariable UUID memberId,
            @Valid @RequestBody RoleModifyRequest request
    );

    @DeleteMapping("/internal/members/{memberId}/roles/{role}")
    ResponseEntity<Void> deleteMemberRole(
            @PathVariable UUID memberId,
            @PathVariable String role
    );
}
