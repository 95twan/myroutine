package com.node5.memberservice.member.application;

import com.node5.memberservice.member.domain.MemberOutbox;

public interface MemberOutboxHandler {

    String getEventType();

    void publish(MemberOutbox outbox);
}
