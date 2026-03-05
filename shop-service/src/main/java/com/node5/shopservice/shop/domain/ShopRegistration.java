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
@Table(name = "\"shop_registration\"", schema = "shop")
public class ShopRegistration extends BaseEntity {

    @Id
    @Column(name = "shop_id")
    private UUID shopId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_registration_status")
    private ShopRegistrationStatus status;

    @Column(name = "failure_reason_code", length = 20)
    private String failureReasonCode;

    @Column(name = "failure_reason_message", length = 100)
    private String failureReasonMessage;

    private ShopRegistration(
            Shop shop,
            ShopRegistrationStatus status
    ) {
        this.shop = shop;
        this.status = status;
    }

    public static ShopRegistration create(Shop shop) {
        return new ShopRegistration(shop, ShopRegistrationStatus.REQUESTED);
    }

    public void shopRegistrationCompleted() {
        if (status == ShopRegistrationStatus.REQUESTED) {
            this.status = ShopRegistrationStatus.COMPLETED;
        }
    }

    public void shopRegistrationFailed(String failureReasonCode, String failureReasonMessage) {
        if (status == ShopRegistrationStatus.REQUESTED) {
            this.status = ShopRegistrationStatus.FAILED;
            this.failureReasonCode = failureReasonCode;
            this.failureReasonMessage = failureReasonMessage;
        }
    }

    public void shopRegistrationDead(String failureReasonCode, String failureReasonMessage) {
        if (status == ShopRegistrationStatus.REQUESTED) {
            this.status = ShopRegistrationStatus.DEAD;
            this.failureReasonCode = failureReasonCode;
            this.failureReasonMessage = failureReasonMessage;
        }
    }
}
