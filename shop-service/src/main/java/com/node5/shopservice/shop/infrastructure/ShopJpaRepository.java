package com.node5.shopservice.shop.infrastructure;

import com.node5.shopservice.shop.domain.Shop;
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
    int countByMemberIdAndDeletedAtIsNullAndIdNot(UUID memberId, UUID shopId);

    @Query("""
            select s
            from Shop s
            join fetch s.registration sr
            left join fetch s.deletion sd
            where s.memberId = :memberId
              and s.deletedAt is null
            """)
    Page<Shop> findAllWithStatusAndDeletedAtIsNull(UUID memberId, Pageable pageable);

    @Query("""
                select s
                from Shop s
                join fetch s.registration sr
                left join fetch s.deletion sd
                where s.id = :shopId
                  and s.memberId = :memberId
                  and s.deletedAt is null
            """)
    Optional<Shop> findByIdWithStatusAndDeletedAtIsNull(UUID shopId, UUID memberId);

    @Query("""
                select s
                from Shop s
                join ShopRegistration sr on sr.shopId = s.id
                where s.id = :shopId
                    and sr.status = :status
                    and s.deletedAt is null
            """)
    Optional<Shop> findByIdAndRegistrationStatusIs(UUID shopId, ShopRegistrationStatus status);
}
