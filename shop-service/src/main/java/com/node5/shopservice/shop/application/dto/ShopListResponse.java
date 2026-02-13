package com.node5.shopservice.shop.application.dto;

import com.node5.shopservice.shop.domain.Shop;

import java.util.UUID;

public record ShopListResponse(
        UUID shopId,
        String shopName,
        String registrationStatus
) {
    public static ShopListResponse from(Shop shop) {
        return new ShopListResponse(shop.getId(), shop.getShopName(), shop.getRegistration().getStatus().name());
    }
}
