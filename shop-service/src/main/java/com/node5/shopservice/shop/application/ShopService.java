package com.node5.shopservice.shop.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.node5.common.event.*;
import com.node5.shopservice.client.WalletClient;
import com.node5.shopservice.shop.application.dto.ShopInfoResponse;
import com.node5.shopservice.shop.application.dto.ShopListResponse;
import com.node5.shopservice.shop.application.dto.ShopModifyCommand;
import com.node5.shopservice.shop.application.dto.ShopRegisterCommand;
import com.node5.shopservice.shop.domain.*;
import com.node5.shopservice.shop.exception.ShopErrorCode;
import com.node5.shopservice.shop.exception.ShopException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopRegistrationRepository shopRegistrationRepository;
    private final ShopDeletionRepository shopDeletionRepository;
    private final ShopOutboxRepository shopOutboxRepository;
    private final WalletClient walletClient;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public Page<ShopListResponse> findMyShopList(UUID memberId, Pageable pageable) {
        return shopRepository.findAllWithStatusAndDeletedAtIsNull(memberId, pageable).map(ShopListResponse::from);
    }

    public ShopInfoResponse findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdWithStatusAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public void registerShop(UUID memberId, ShopRegisterCommand command) {
        shopRepository.getTxLock(memberId);
        checkWalletExists(memberId);

        Shop shop = shopRepository.save(Shop.create(memberId, command));
        shopRegistrationRepository.save(ShopRegistration.create(shop));

        MemberRoleChangeRequestedEvent memberRoleChangeRequestedEvent = new MemberRoleChangeRequestedEvent(
                shop.getId(),
                memberId,
                MemberRoleChangeAction.ADD_SELLER,
                MemberRoleChangeSagaType.SHOP_REGISTRATION
        );

        String payload;
        try {
            payload = objectMapper.writeValueAsString(memberRoleChangeRequestedEvent);
        } catch (JsonProcessingException e) {
            throw new ShopException(ShopErrorCode.JSON_PROCESSING_EXCEPTION);
        }

        ShopOutbox shopOutbox = ShopOutbox.create("MemberRoleChangeRequestedEvent", memberId, payload);
        shopOutboxRepository.save(shopOutbox);
    }

    private void checkWalletExists(UUID memberId) {
        try {
            walletClient.getWallet(memberId);
        } catch (FeignException.NotFound e) {
            throw new ShopException(ShopErrorCode.WALLET_REQUIRED);
        } catch (Exception e) {
            log.error("billingClient.getWallet error", e);
            throw new ShopException(ShopErrorCode.UNCAUGHT_EXCEPTION);
        }
    }

    @Transactional
    public void modifyMyShopInfo(UUID memberId, UUID shopId, ShopModifyCommand command) {
        shopRepository.getTxLock(memberId);
        Shop shop = shopRepository.findByIdWithStatusAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        if (shop.getRegistration() != null && shop.getRegistration().getStatus() == ShopRegistrationStatus.REQUESTED) {
            throw new ShopException(ShopErrorCode.SHOP_IS_REGISTERING);
        }

        if (shop.getDeletion() != null && shop.getDeletion().getStatus() == ShopDeletionStatus.REQUESTED) {
            throw new ShopException(ShopErrorCode.SHOP_IS_DELETING);
        }

        shop.update(command);
    }

    @Transactional
    public void deleteMyShop(UUID memberId, UUID shopId) {
        shopRepository.getTxLock(memberId);
        Shop shop = shopRepository.findByIdWithStatusAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));

        // 멱등 처리
        if (shop.getDeletion() != null) {
            ShopDeletionStatus status = shop.getDeletion().getStatus();
            if (status == ShopDeletionStatus.FAILED || status == ShopDeletionStatus.DEAD) {
                throw new ShopException(ShopErrorCode.SHOP_DELETE_NOT_ALLOWED);
            }
            if (status == ShopDeletionStatus.REQUESTED || status == ShopDeletionStatus.COMPLETED) {
                return;
            }
        }

        if (shop.getRegistration() == null || shop.getRegistration().getStatus() != ShopRegistrationStatus.COMPLETED) {
            throw new ShopException(ShopErrorCode.SHOP_DELETE_NOT_ALLOWED);
        }

        shopDeletionRepository.save(ShopDeletion.create(shop, ShopDeletionStatus.REQUESTED));

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNullAndIdNot(memberId, shop.getId());
        if (shopCount != 0) {
            deleteShopCompleted(shop.getId());
        } else {
            MemberRoleChangeRequestedEvent memberRoleChangeRequestedEvent = new MemberRoleChangeRequestedEvent(
                    shop.getId(),
                    memberId,
                    MemberRoleChangeAction.REMOVE_SELLER,
                    MemberRoleChangeSagaType.SHOP_DELETION
            );

            String payload;
            try {
                payload = objectMapper.writeValueAsString(memberRoleChangeRequestedEvent);
            } catch (JsonProcessingException e) {
                throw new ShopException(ShopErrorCode.JSON_PROCESSING_EXCEPTION);
            }

            ShopOutbox shopOutbox = ShopOutbox.create("MemberRoleChangeRequestedEvent", memberId, payload);
            shopOutboxRepository.save(shopOutbox);
        }
    }

    public UUID getMemberIdByShopId(UUID shopId) {
        Shop shop = shopRepository.findByIdAndRegistrationStatusIs(shopId, ShopRegistrationStatus.COMPLETED).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND)
        );

        return shop.getMemberId();
    }

    // Todo - 지금 delete 변경은 각 row 마다 변경 상점이 많아진다면 bulk update를 고려
    @Transactional
    public void deleteAllMyShop(UUID memberId) {
        List<Shop> shops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        shops.forEach(Shop::delete);
    }

    public List<UUID> getShopIdsByMemberId(UUID memberId) {
        List<Shop> shops = shopRepository.findAllByMemberIdAndDeletedAtIsNull(memberId);
        return shops.stream().map(Shop::getId).toList();
    }

    @Transactional
    public void registerShopCompleted(UUID shopId) {
        ShopRegistration shopRegistration = shopRegistrationRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_REGISTRATION_NOT_FOUND)
        );

        shopRegistration.shopRegistrationCompleted();
    }

    @Transactional
    public void registerShopFailed(MemberRoleChangeFailedEvent event) {
        ShopRegistration shopRegistration = shopRegistrationRepository.findByShopId(event.shopId()).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_REGISTRATION_NOT_FOUND)
        );

        shopRegistration.shopRegistrationFailed(event.reasonCode(), event.reasonMessage());
    }

    @Transactional
    public void registerShopDead(MemberRoleChangeDeadEvent event) {
        ShopRegistration shopRegistration = shopRegistrationRepository.findByShopId(event.shopId()).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_REGISTRATION_NOT_FOUND)
        );

        shopRegistration.shopRegistrationDead(event.reasonCode(), event.reasonMessage());
    }

    @Transactional
    public void deleteShopCompleted(UUID shopId) {
        ShopDeletion shopDeletion = shopDeletionRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_DELETION_NOT_FOUND)
        );

        if (!shopDeletion.shopDeletionCompleted()) {return;}

        Shop shop = shopDeletion.getShop();
        shop.delete();
        ShopDeletedEvent shopDeletedEvent = new ShopDeletedEvent(shopId);
        eventPublisher.publishEvent(shopDeletedEvent);
    }

    @Transactional
    public void deleteShopFailed(MemberRoleChangeFailedEvent event) {
        ShopDeletion shopDeletion = shopDeletionRepository.findByShopId(event.shopId()).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_DELETION_NOT_FOUND)
        );

        shopDeletion.shopDeletionFailed(event.reasonCode(), event.reasonMessage());
    }

    @Transactional
    public void deleteShopDead(MemberRoleChangeDeadEvent event) {
        ShopDeletion shopDeletion = shopDeletionRepository.findByShopId(event.shopId()).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_DELETION_NOT_FOUND)
        );

        shopDeletion.shopDeletionDead(event.reasonCode(), event.reasonMessage());
    }
}
