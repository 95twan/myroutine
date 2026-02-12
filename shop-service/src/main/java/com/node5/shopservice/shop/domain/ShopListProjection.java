package com.node5.shopservice.shop.domain;

import java.util.UUID;

public interface ShopListProjection {
    UUID getId();

    String getShopName();

    ShopRegistrationStatus getRegistrationStatus();
}
