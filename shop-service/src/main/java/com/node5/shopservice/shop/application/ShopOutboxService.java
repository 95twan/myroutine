package com.node5.shopservice.shop.application;

import com.node5.shopservice.shop.domain.ShopOutbox;
import com.node5.shopservice.shop.domain.ShopOutboxRepository;
import com.node5.shopservice.shop.domain.ShopOutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShopOutboxService {

    private final ShopOutboxRepository shopOutboxRepository;
    private final Map<String, ShopOutboxHandler> shopOutboxHandlerMap;

    public ShopOutboxService(
            ShopOutboxRepository shopOutboxRepository,
            List<ShopOutboxHandler> shopOutboxHandlerList
    ) {
        this.shopOutboxRepository = shopOutboxRepository;
        this.shopOutboxHandlerMap = shopOutboxHandlerList.stream().collect(Collectors.toMap(ShopOutboxHandler::getEventType, handler -> handler));

    }

    @Transactional
    public List<ShopOutbox> claimReadyBatch(Pageable pageable) {
        List<ShopOutbox> shopOutboxes = shopOutboxRepository.getBatchForUpdateByStatus(ShopOutboxStatus.READY, pageable);
        for (ShopOutbox shopOutbox : shopOutboxes) {
            shopOutbox.shopOutboxProcessing();
        }

        return shopOutboxes;
    }

    @Transactional
    public void publish(ShopOutbox shopOutbox) {
        ShopOutboxHandler shopOutboxHandler = shopOutboxHandlerMap.get(shopOutbox.getEventType());

        if (shopOutboxHandler == null) {
            shopOutbox.incrementRetryCount();

            if (shopOutbox.getRetryCount() >= 3) {
                shopOutbox.shopOutboxFailed();
            } else {
                shopOutbox.shopOutboxReady();
            }

            shopOutboxRepository.save(shopOutbox);
            return;
        }

        try {
            shopOutboxHandler.publish(shopOutbox);
            shopOutbox.shopOutboxSent();
        } catch (Exception e) {
            shopOutbox.incrementRetryCount();

            if (shopOutbox.getRetryCount() >= 3) {
                shopOutbox.shopOutboxFailed();
            } else {
                shopOutbox.shopOutboxReady();
            }
        }

        shopOutboxRepository.save(shopOutbox);
    }
}
