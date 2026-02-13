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
    public Page<Shop> findAllWithRegistration(UUID memberId, Pageable pageable) {
        return shopJpaRepository.findAllWithRegistration(memberId, pageable);
    }

    @Override
    public Optional<Shop> findByIdWithRegistration(UUID shopId, UUID memberId) {
        return shopJpaRepository.findByIdWithRegistration(shopId, memberId);
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
    public Optional<Shop> findByIdAndMemberIdAndDeletedAtIsNull(UUID shopId, UUID memberId) {
        return shopJpaRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId);
    }

    @Override
    public int countByMemberIdAndDeletedAtIsNull(UUID memberId) {
        return shopJpaRepository.countByMemberIdAndDeletedAtIsNull(memberId);
    }

    @Override
    public void flush() {
        shopJpaRepository.flush();
    }

    @Override
    public Optional<Shop> findByIdAndStatusIsCompleted(UUID shopId) {
        return shopJpaRepository.findByIdAndStatusIsCompleted(shopId, ShopRegistrationStatus.COMPLETED);
    }
}
