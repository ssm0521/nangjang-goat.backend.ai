package com.naengjang_goat.inventory_system.analysis.repository;

import com.naengjang_goat.inventory_system.analysis.domain.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // 원재료 ID 기준 최근 30일
    List<PriceHistory> findTop30ByRawMaterialIdOrderByPriceDateDesc(Long rawMaterialId);

    // 기간 조회
    List<PriceHistory> findAllByRawMaterialIdAndPriceDateBetween(
            Long rawMaterialId,
            LocalDate start,
            LocalDate end
    );

    // 🔥 (AI 예측용) productName 기준 최근 30일
    List<PriceHistory> findTop30ByProductNameOrderByPriceDateDesc(String productName);

    // 가장 최신 1건
    Optional<PriceHistory> findTop1ByProductNameOrderByPriceDateDesc(String productName);
}
