package com.node5.shopservice.shop.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopRepository {
    Page<ShopListProjection> findAllWithRegistration(UUID memberId, Pageable pageable);
    Optional<ShopInfoProjection> findByIdWithRegistration(UUID shopId, UUID memberId);
    List<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId);
    Shop save(Shop shop);
    Optional<Shop> findByIdAndMemberIdAndDeletedAtIsNull(UUID shopId, UUID memberId);
    int countByMemberIdAndDeletedAtIsNull(UUID memberId);
    void flush();
    Optional<Shop> findByIdAndStatusIsCompleted(UUID shopId);
}
