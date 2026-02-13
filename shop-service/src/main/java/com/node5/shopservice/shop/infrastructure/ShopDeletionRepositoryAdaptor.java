package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopDeletion;
import com.node5.shopservice.shop.domain.ShopDeletionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ShopDeletionRepositoryAdaptor implements ShopDeletionRepository {

    private final ShopDeletionJpaRepository shopDeletionJpaRepository;

    @Override
    public ShopDeletion save(ShopDeletion shopDeletion) {
        return shopDeletionJpaRepository.save(shopDeletion);
    }

    @Override
    public Optional<ShopDeletion> findByShopId(UUID shopId) {
        return shopDeletionJpaRepository.findByShopId(shopId);
    }
}
