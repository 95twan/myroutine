package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ShopRepositoryAdaptor implements ShopRepository {

    private final ShopJpaRepository shopJpaRepository;

    @Override
    public Page<Shop> findAllWithStatusAndDeletedAtIsNull(UUID memberId, Pageable pageable) {
        return shopJpaRepository.findAllWithStatusAndDeletedAtIsNull(memberId, pageable);
    }

    @Override
    public Optional<Shop> findByIdWithStatusAndDeletedAtIsNull(UUID shopId, UUID memberId) {
        return shopJpaRepository.findByIdWithStatusAndDeletedAtIsNull(shopId, memberId);
    }

    @Override
    public Optional<Shop> findByIdWithRegistrationAndDeletion(UUID shopId, UUID memberId) {
        return shopJpaRepository.findByIdWithRegistrationAndDeletion(shopId, memberId);
    }

    @Override
    public List<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId) {
        return shopJpaRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
    }

    @Override
    public Shop save(Shop shop) {
        return shopJpaRepository.save(shop);
    }

    @Override
    public Optional<Shop> findByIdAndRegistrationStatusIs(UUID shopId, ShopRegistrationStatus status) {
        return shopJpaRepository.findByIdAndRegistrationStatusIs(shopId, status);
    }

    @Override
    public int countByMemberIdAndDeletedAtIsNullAndIdNot(UUID memberId, UUID shopId) {
        return shopJpaRepository.countByMemberIdAndDeletedAtIsNullAndIdNot(memberId, shopId);
    }

    @Override
    public void getTxLock(UUID memberId) {
        shopJpaRepository.getTxLock(memberId);
    }
}
