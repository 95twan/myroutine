package com.node5.shopservice.shop.domain;

import com.node5.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "\"shop_deletion\"", schema = "shop")
public class ShopDeletion extends BaseEntity {

    @Id
    @Column(name = "shop_id")
    private UUID shopId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_deletion_status")
    private ShopDeletionStatus status;

    private ShopDeletion(
            Shop shop,
            ShopDeletionStatus status
    ) {
        this.shop = shop;
        this.status = status;
    }

    public static ShopDeletion create(Shop shop, ShopDeletionStatus status) {
        return new ShopDeletion(shop, status);
    }

    public boolean shopDeletionCompleted() {
        if (status == ShopDeletionStatus.REQUESTED) {
            this.status = ShopDeletionStatus.COMPLETED;
            return true;
        }
        return false;
    }

    public void shopDeletionFailed() {
        if (status == ShopDeletionStatus.REQUESTED) {
            this.status = ShopDeletionStatus.FAILED;
        }
    }
}
