package com.node5.shopservice.shop.domain;

import java.util.UUID;

public interface ShopInfoProjection {
    UUID getId();

    String getShopName();

    String getShopEmail();

    String getShopPhoneNumber();

    String getShopAddress();

    ShopRegistrationStatus getRegistrationStatus();
}
