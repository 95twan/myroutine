package com.node5.shopservice.shop.outbox;

import com.node5.shopservice.shop.application.ShopOutboxService;
import com.node5.shopservice.shop.domain.ShopOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShopOutboxJobRunner {

    private final ShopOutboxService shopOutboxService;

    public int runOnce() {
        List<ShopOutbox> outboxes =
                shopOutboxService.claimReadyBatch(PageRequest.of(0, 100));

        int processed = 0;
        for (ShopOutbox outbox : outboxes) {
            shopOutboxService.publish(outbox);
            processed++;
        }
        return processed;
    }
}
