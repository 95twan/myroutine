package com.node5.common.event;

import java.util.UUID;

public record ShopRegisteredEvent(
        UUID shopId,
        UUID memberId
) {
}
