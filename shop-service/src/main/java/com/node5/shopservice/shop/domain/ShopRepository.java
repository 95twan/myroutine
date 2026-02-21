package com.node5.shopservice.shop.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    Page<Shop> findAllWithStatusAndDeletedAtIsNull(UUID memberId, Pageable pageable);
    Optional<Shop> findByIdWithStatusAndDeletedAtIsNull(UUID shopId, UUID memberId);
    List<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId);
    Shop save(Shop shop);
    Optional<Shop> findByIdAndRegistrationStatusIs(UUID shopId, ShopRegistrationStatus status);

    int countByMemberIdAndDeletedAtIsNullAndIdNot(UUID memberId, UUID id);

    void getTxLock(UUID memberId);
}
