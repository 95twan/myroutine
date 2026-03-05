package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopRegistration;
import com.node5.shopservice.shop.domain.ShopRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ShopRegistrationRepositoryAdaptor implements ShopRegistrationRepository {

    private final ShopRegistrationJpaRepository shopRegistrationJpaRepository;

    @Override
    public ShopRegistration save(ShopRegistration shopRegistration) {
        return shopRegistrationJpaRepository.save(shopRegistration);
    }

    @Override
    public Optional<ShopRegistration> findByShopId(UUID shopId) {
        return shopRegistrationJpaRepository.findByShopId(shopId);
    }
}
