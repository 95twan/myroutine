package com.node5.shopservice.shop.domain;

import java.util.Optional;
import java.util.UUID;

public interface ShopDeletionRepository {
    ShopDeletion save(ShopDeletion shopDeletion);
    Optional<ShopDeletion> findByShopId(UUID shopId);
}
