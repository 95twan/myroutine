package com.node5.memberservice.member.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberOutboxRepository {
    MemberOutbox save(MemberOutbox shopOutbox);

    List<MemberOutbox> getBatchForUpdateByStatus(MemberOutboxStatus status, Pageable pageable);
}
