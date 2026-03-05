package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopDeletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShopDeletionJpaRepository extends JpaRepository<ShopDeletion, UUID> {
    Optional<ShopDeletion> findByShopId(UUID shopId);
}
