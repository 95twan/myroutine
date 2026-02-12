package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShopRegistrationJpaRepository extends JpaRepository<ShopRegistration, UUID> {
    Optional<ShopRegistration> findByShopId(UUID shopId);
}
