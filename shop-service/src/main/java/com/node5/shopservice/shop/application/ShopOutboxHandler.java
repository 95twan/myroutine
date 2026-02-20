package com.node5.shopservice.shop.application;

import com.node5.shopservice.shop.domain.ShopOutbox;

public interface ShopOutboxHandler {

    String getEventType();

    void publish(ShopOutbox outbox);
}
