package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.ShopRegistration;
import com.node5.shopservice.shop.domain.ShopRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShopRegistrationRepositoryAdaptor implements ShopRegistrationRepository {

    private final ShopRegistrationJpaRepository shopRegistrationJpaRepository;


    @Override
    public void save(ShopRegistration shopRegistration) {
        shopRegistrationJpaRepository.save(shopRegistration);
    }
}
