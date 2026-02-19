package com.node5.shopservice.shop.application.dto;

import com.node5.shopservice.shop.domain.Shop;

import java.util.UUID;

public record ShopListResponse(
        UUID shopId,
        String shopName,
        String status
) {
    public static ShopListResponse from(Shop shop) {
        String status;

        if (shop.getDeletion() != null) {
            status = switch (shop.getDeletion().getStatus()) {
                case COMPLETED -> "DELETED";
                case REQUESTED -> "DELETING";
                case FAILED -> "DELETE_FAILED";
                case DEAD -> "DELETE_DEAD";
            };
        } else  {
            status = switch (shop.getRegistration().getStatus()) {
                case COMPLETED -> "ACTIVE";
                case REQUESTED -> "REGISTERING";
                case FAILED -> "REGISTER_FAILED";
                case DEAD -> "REGISTER_DEAD";
            };
        }

        return new ShopListResponse(shop.getId(), shop.getShopName(), status);
    }
}
