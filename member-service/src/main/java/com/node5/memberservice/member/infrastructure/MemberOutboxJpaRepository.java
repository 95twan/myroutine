package com.node5.memberservice.member.infrastructure;

import com.node5.memberservice.member.domain.MemberOutbox;
import com.node5.memberservice.member.domain.MemberOutboxStatus;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MemberOutboxJpaRepository extends JpaRepository<MemberOutbox, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")) // SKIP LOCKED
    @Query("""
        select so
        from MemberOutbox so
        where so.status = :status
        order by so.createdAt asc
    """)
    List<MemberOutbox> getBatchForUpdateByStatus(@Param("status") MemberOutboxStatus status, Pageable pageable);
}
