package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.Shop;
import com.node5.shopservice.shop.domain.ShopInfoProjection;
import com.node5.shopservice.shop.domain.ShopListProjection;
import com.node5.shopservice.shop.domain.ShopRegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopJpaRepository extends JpaRepository<Shop, UUID> {
    List<Shop> findAllByMemberIdAndDeletedAtIsNull(UUID memberId);
    int countByMemberIdAndDeletedAtIsNull(UUID memberId);

    Optional<Shop> findByIdAndMemberIdAndDeletedAtIsNull(UUID shopId, UUID memberId);

    @Query("""
                select s.id as id, s.shopName as shopName, sr.status as registrationStatus
                from Shop s
                join ShopRegistration sr on sr.shopId = s.id
                where s.memberId = :memberId
                    and s.deletedAt is null
            """)
    Page<ShopListProjection> findAllWithRegistration(UUID memberId, Pageable pageable);

    @Query("""
                select s.id as id, s.shopName as shopName, s.shopEmail as shopEmail, s.shopPhoneNumber as shopPhoneNumber, s.shopAddress as shopAddress, sr.status as registrationStatus
                from Shop s
                join ShopRegistration sr on sr.shopId = s.id
                where s.id = :shopId
                    and s.memberId = :memberId
                    and s.deletedAt is null
            """)
    Optional<ShopInfoProjection> findByIdWithRegistration(UUID shopId, UUID memberId);

    @Query("""
                select s
                from Shop s
                join ShopRegistration sr on sr.shopId = s.id
                where s.id = :shopId
                    and sr.status = :status
                    and s.deletedAt is null
            """)
    Optional<Shop> findByIdAndStatusIsCompleted(UUID shopId, ShopRegistrationStatus status);
}
