package com.node5.common.event;

import java.util.UUID;

public record MemberRoleChangeRequestedEvent(
        UUID shopId,
        UUID memberId,
        MemberRoleChangeAction action,
        MemberRoleChangeSagaType sagaType
) {
}
