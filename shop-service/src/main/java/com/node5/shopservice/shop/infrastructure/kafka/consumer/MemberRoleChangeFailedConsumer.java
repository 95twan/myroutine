package com.node5.shopservice.shop.infrastructure.kafka.consumer;

import com.node5.common.event.MemberRoleChangeFailedEvent;
import com.node5.common.event.MemberRoleChangeSagaType;
import com.node5.shopservice.shop.application.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRoleChangeFailedConsumer {

    private final ShopService shopService;

    @KafkaListener(
            topics = "${kafka.topics.member-role-change-failed}",
            containerFactory = "retryKafkaListenerContainerFactory"
    )
    public void consume(MemberRoleChangeFailedEvent event, Acknowledgment ack) {
        if (event.sagaType() == MemberRoleChangeSagaType.SHOP_REGISTRATION) {
            shopService.registerShopFailed(event);
        } else if (event.sagaType() == MemberRoleChangeSagaType.SHOP_DELETION) {
            shopService.deleteShopFailed(event);
        }
        ack.acknowledge();
    }
}
