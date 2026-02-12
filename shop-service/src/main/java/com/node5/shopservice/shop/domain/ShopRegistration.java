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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_registration_status")
    private ShopRegistrationStatus status;

    private ShopRegistration(
            Shop shop,
            ShopRegistrationStatus shopRegistrationStatus
    ) {
        this.shop = shop;
        this.status = shopRegistrationStatus;
    }

    public static ShopRegistration create(Shop shop) {
        return new ShopRegistration(
                shop,
                ShopRegistrationStatus.REGISTERING
        );
    }

    public void shopRegistrationActive() {
        this.status = ShopRegistrationStatus.ACTIVE;
    }

    public void shopRegistrationFailed() {
        this.status = ShopRegistrationStatus.FAILED;
    }
}
