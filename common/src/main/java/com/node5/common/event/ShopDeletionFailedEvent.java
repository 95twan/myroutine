package com.node5.common.event;

import java.util.UUID;

public record ShopDeletionFailedEvent(
        UUID shopId,
        String reasonCode,
        String reasonMessage
) {
}
