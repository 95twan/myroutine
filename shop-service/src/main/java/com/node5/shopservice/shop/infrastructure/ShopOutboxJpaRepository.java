package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopOutbox;
import com.node5.shopservice.shop.domain.ShopOutboxStatus;
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

public interface ShopOutboxJpaRepository extends JpaRepository<ShopOutbox, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")) // SKIP LOCKED
    @Query("""
        select so
        from ShopOutbox so
        where so.status = :status
        order by so.createdAt asc
    """)
    List<ShopOutbox> getBatchForUpdateByStatus(@Param("status") ShopOutboxStatus status, Pageable pageable);
}
