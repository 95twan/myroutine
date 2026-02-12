package com.node5.shopservice.shop.application.dto;


import com.node5.shopservice.shop.domain.ShopInfoProjection;

import java.util.UUID;

public record ShopInfoResponse(
        UUID id,
        String shopName,
        String shopEmail,
        String shopPhoneNumber,
        String shopAddress,
        String registrationStatus
) {
    public static ShopInfoResponse from(ShopInfoProjection projection) {

        return new ShopInfoResponse(
                projection.getId(),
                projection.getShopName(),
                projection.getShopEmail(),
                projection.getShopPhoneNumber(),
                projection.getShopAddress(),
                projection.getRegistrationStatus().name()
        );
    }
}
