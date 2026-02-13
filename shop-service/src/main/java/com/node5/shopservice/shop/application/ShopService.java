package com.node5.shopservice.shop.application;

import com.node5.common.event.ShopDeletedEvent;
import com.node5.common.event.ShopRegistrationRequestedEvent;
import com.node5.shopservice.client.MemberClient;
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

    private static final String ROLE_SELLER = "SELLER";

    private final ShopRepository shopRepository;
    private final ShopRegistrationRepository shopRegistrationRepository;
    private final WalletClient walletClient;
    private final MemberClient memberClient;
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
        ShopRegistration shopRegistration = shopRegistrationRepository.save(ShopRegistration.create(shop));
        shop.setRegistration(shopRegistration);

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
        Shop shop = shopRepository.findByIdWithRegistration(shopId, memberId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.SHOP_NOT_FOUND));
        if (shop.getRegistration() == null || shop.getRegistration().getStatus() != ShopRegistrationStatus.COMPLETED) {
            throw new ShopException(ShopErrorCode.SHOP_DELETE_NOT_ALLOWED);
        }

        ShopDeletedEvent shopDeletedEvent = new ShopDeletedEvent(shop.getId());

        shop.delete();
        shopRepository.flush();

        int shopCount = shopRepository.countByMemberIdAndDeletedAtIsNull(memberId);
        if (shopCount == 0) {
            try {
                memberClient.deleteMemberRole(memberId, ROLE_SELLER);
            } catch (Exception e) {
                log.error("memberClient.updateMemberRoles error : {}", e.getMessage());
                throw new ShopException(ShopErrorCode.ROLE_UPDATE_FAILED);
            }
        }

        // 가게 삭제 topic 발행
        eventPublisher.publishEvent(shopDeletedEvent);
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
}
