package com.node5.shopservice.shop.application;

import com.node5.common.event.ShopDeletedEvent;
import com.node5.common.event.ShopDeletionRequestedEvent;
import com.node5.common.event.ShopRegistrationRequestedEvent;
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
    private final WalletClient walletClient;
    private final ApplicationEventPublisher eventPublisher;

    public Page<ShopListResponse> findMyShopList(UUID memberId, Pageable pageable) {
        return shopRepository.findAllWithRegistration(memberId, pageable).map(ShopListResponse::from);
    }

    public ShopInfoResponse findMyShopInfo(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdWithRegistration(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        return ShopInfoResponse.from(shop);
    }

    @Transactional
    public void registerShop(UUID memberId, ShopRegisterCommand command) {
        checkWalletExists(memberId);

        Shop shop = shopRepository.save(Shop.create(memberId, command));
        shopRegistrationRepository.save(ShopRegistration.create(shop));

        ShopRegistrationRequestedEvent shopRegistrationRequestedEvent = new ShopRegistrationRequestedEvent(shop.getId(), memberId);
        eventPublisher.publishEvent(shopRegistrationRequestedEvent);
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
        Shop shop = shopRepository.findByIdAndMemberIdAndDeletedAtIsNull(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        shop.update(command);
    }

    @Transactional
    public void deleteMyShop(UUID memberId, UUID shopId) {
        Shop shop = shopRepository.findByIdWithRegistrationAndDeletion(shopId, memberId)
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

        if (shop.getDeletedAt() != null) {
            return;
        }

        if (shop.getRegistration() == null || shop.getRegistration().getStatus() != ShopRegistrationStatus.COMPLETED) {
            throw new ShopException(ShopErrorCode.SHOP_DELETE_NOT_ALLOWED);
        }

        shop.delete();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        if (shopCount != 0) {
            shopDeletionRepository.save(ShopDeletion.create(shop, ShopDeletionStatus.COMPLETED));
            ShopDeletedEvent shopDeletedEvent = new ShopDeletedEvent(shop.getId());
            eventPublisher.publishEvent(shopDeletedEvent);
        } else {
            shopDeletionRepository.save(ShopDeletion.create(shop, ShopDeletionStatus.REQUESTED));
            ShopDeletionRequestedEvent shopDeletionRequestedEvent = new ShopDeletionRequestedEvent(shop.getId(), memberId);
            eventPublisher.publishEvent(shopDeletionRequestedEvent);
        }
    }

    public UUID getMemberIdByShopId(UUID shopId) {
        Shop shop = shopRepository.findByIdAndStatusIsCompleted(shopId).orElseThrow(
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
    public void registerShopFailed(UUID shopId) {
        ShopRegistration shopRegistration = shopRegistrationRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_REGISTRATION_NOT_FOUND)
        );

        shopRegistration.shopRegistrationFailed();
    }

    @Transactional
    public void registerShopDead(UUID shopId) {
        ShopRegistration shopRegistration = shopRegistrationRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_REGISTRATION_NOT_FOUND)
        );

        shopRegistration.shopRegistrationDead();
    }

    @Transactional
    public void deleteShopCompleted(UUID shopId) {
        ShopDeletion shopDeletion = shopDeletionRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_DELETION_NOT_FOUND)
        );

        if(shopDeletion.shopDeletionCompleted()) {
            ShopDeletedEvent shopDeletedEvent = new ShopDeletedEvent(shopId);
            eventPublisher.publishEvent(shopDeletedEvent);
        }
    }

    @Transactional
    public void deleteShopFailed(UUID shopId) {
        ShopDeletion shopDeletion = shopDeletionRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_DELETION_NOT_FOUND)
        );

        shopDeletion.shopDeletionFailed();
    }

    @Transactional
    public void deleteShopDead(UUID shopId) {
        ShopDeletion shopDeletion = shopDeletionRepository.findByShopId(shopId).orElseThrow(
                () -> new ShopException(ShopErrorCode.SHOP_DELETION_NOT_FOUND)
        );

        shopDeletion.shopDeletionDead();
    }
}
