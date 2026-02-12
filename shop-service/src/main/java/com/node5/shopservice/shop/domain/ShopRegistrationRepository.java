package com.node5.shopservice.shop.domain;

import java.util.Optional;
import java.util.UUID;

public interface ShopRegistrationRepository {
    void save(ShopRegistration shopRegistration);
    Optional<ShopRegistration> findByShopId(UUID shopId);
}
