package com.node5.common.event;

import java.util.UUID;

public record MemberRoleChangeDeadEvent(
        UUID shopId,
        UUID memberId,
        MemberRoleChangeSagaType sagaType,
        String reasonCode,
        String reasonMessage
) {
}
