package com.node5.common.event;

import java.util.UUID;

public record MemberRoleChangeFailedEvent(
        UUID shopId,
        UUID memberId,
        MemberRoleChangeSagaType sagaType,
        String reasonCode,
        String reasonMessage
) {
}
