package com.node5.shopservice.settlement.infrastructure;

import com.node5.shopservice.settlement.domain.SettlementProcessStatus;
import com.node5.shopservice.settlement.domain.SettlementSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementSourceJpaRepository extends JpaRepository<SettlementSource, UUID> {

    boolean existsByShopIdInAndStatus(List<UUID> shopIdList, SettlementProcessStatus processStatus);
}
