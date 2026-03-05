package com.node5.common.event;

import java.util.UUID;

public record ShopRegistrationRequestedEvent(
        UUID shopId,
        UUID memberId
) {
}
