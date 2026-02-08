package com.node5.shopservice.client;

import com.node5.shopservice.client.dto.WalletInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "wallet-service")
public interface WalletClient {

    @GetMapping("/internal/wallets")
    ResponseEntity<WalletInfo> getWallet(@RequestHeader("Member-Id") UUID memberId);
}
