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

    @Column(name = "failure_reason_code", length = 20)
    private String failureReasonCode;

    @Column(name = "failure_reason_message", length = 100)
    private String failureReasonMessage;

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

    public void shopDeletionFailed(String failureReasonCode, String failureReasonMessage) {
        if (status == ShopDeletionStatus.REQUESTED) {
            this.status = ShopDeletionStatus.FAILED;
            this.failureReasonCode = failureReasonCode;
            this.failureReasonMessage = failureReasonMessage;
        }
    }

    public void shopDeletionDead(String failureReasonCode, String failureReasonMessage) {
        if (status == ShopDeletionStatus.REQUESTED) {
            this.status = ShopDeletionStatus.DEAD;
            this.failureReasonCode = failureReasonCode;
            this.failureReasonMessage = failureReasonMessage;
        }
    }
}
