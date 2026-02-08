package com.node5.memberservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shop-service")
public interface ShopClient {
    @GetMapping("/internal/shops/ids")
    ResponseEntity<List<UUID>> getShopIdsByMemberId(@RequestParam UUID memberId);

    @PostMapping("/internal/settlements/in-progress")
    ResponseEntity<Boolean> hasInProgressSettlement(@RequestBody List<UUID> shopIdList);
}
