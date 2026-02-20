package com.node5.memberservice.member.infrastructure;

import com.node5.memberservice.member.domain.MemberOutbox;
import com.node5.memberservice.member.domain.MemberOutboxRepository;
import com.node5.memberservice.member.domain.MemberOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberOutboxRepositoryAdaptor implements MemberOutboxRepository {

    private final MemberOutboxJpaRepository memberOutboxJpaRepository;

    @Override
    public MemberOutbox save(MemberOutbox memberOutbox) {
        return memberOutboxJpaRepository.save(memberOutbox);
    }

    @Override
    public List<MemberOutbox> getBatchForUpdateByStatus(MemberOutboxStatus status, Pageable pageable) {
        return memberOutboxJpaRepository.getBatchForUpdateByStatus(status, pageable);
    }

}
