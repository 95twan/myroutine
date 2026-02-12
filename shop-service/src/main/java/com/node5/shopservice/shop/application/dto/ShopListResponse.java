package com.node5.shopservice.shop.application.dto;


import com.node5.shopservice.shop.domain.ShopListProjection;

import java.util.UUID;

public record ShopListResponse(
        UUID shopId,
        String shopName,
        String registrationStatus
) {
    public static ShopListResponse from(ShopListProjection projection) {
        return new ShopListResponse(projection.getId(), projection.getShopName(), projection.getRegistrationStatus().name());
    }
}
