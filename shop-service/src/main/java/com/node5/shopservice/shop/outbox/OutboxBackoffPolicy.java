package com.node5.shopservice.shop.outbox;

import org.springframework.stereotype.Component;

@Component
public class OutboxBackoffPolicy {

    private static final long BASE_DELAY_MS = 500L;   // 500ms
    private static final long MAX_DELAY_MS = 30000L;   // 30s
    private static final int MAX_STREAK = 10;

    public long nextDelayMs(int processedCount, int emptyStreak) {
        if (processedCount > 0) {
            return BASE_DELAY_MS;
        }
        long delay = BASE_DELAY_MS << Math.min(emptyStreak, MAX_STREAK);
        return Math.min(delay, MAX_DELAY_MS);
    }

    public int nextEmptyStreak(int processedCount, int emptyStreak) {
        if (processedCount > 0) {
            return 0;
        }
        return Math.min(emptyStreak + 1, MAX_STREAK);
    }

}
