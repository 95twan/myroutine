package com.node5.common.event;

import java.util.UUID;

public record MemberRoleChangeCompletedEvent(
        UUID shopId,
        UUID memberId,
        MemberRoleChangeSagaType sagaType
) {
}
