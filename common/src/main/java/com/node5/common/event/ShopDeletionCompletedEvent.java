package com.node5.common.event;

import java.util.UUID;

public record ShopDeletionCompletedEvent(
        UUID shopId
) {
}
