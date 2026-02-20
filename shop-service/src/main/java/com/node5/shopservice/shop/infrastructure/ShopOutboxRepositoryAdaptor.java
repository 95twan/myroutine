package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopOutbox;
import com.node5.shopservice.shop.domain.ShopOutboxRepository;
import com.node5.shopservice.shop.domain.ShopOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ShopOutboxRepositoryAdaptor implements ShopOutboxRepository {

    private final ShopOutboxJpaRepository shopOutboxJpaRepository;

    @Override
    public ShopOutbox save(ShopOutbox shopOutbox) {
        return shopOutboxJpaRepository.save(shopOutbox);
    }

    @Override
    public List<ShopOutbox> getBatchForUpdateByStatus(ShopOutboxStatus shopOutboxStatus, Pageable pageable) {
        return shopOutboxJpaRepository.getBatchForUpdateByStatus(shopOutboxStatus, pageable);
    }

}
