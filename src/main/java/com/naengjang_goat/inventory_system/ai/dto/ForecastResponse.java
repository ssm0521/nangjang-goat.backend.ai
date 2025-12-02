package com.naengjang_goat.inventory_system.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponse {
    private String productName;
    private double predictedPrice;    // 내일 예상 가격
    private double avg7;              // 최근 7일 평균
    private double avg30;             // 최근 30일 평균
}
