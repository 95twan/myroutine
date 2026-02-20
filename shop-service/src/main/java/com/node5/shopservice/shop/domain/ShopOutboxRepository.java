package com.node5.shopservice.shop.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShopOutboxRepository {
    ShopOutbox save(ShopOutbox shopOutbox);

    List<ShopOutbox> getBatchForUpdateByStatus(ShopOutboxStatus shopOutboxStatus, Pageable pageable);
}
