package com.naengjang_goat.inventory_system.ai.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PriceAnalysisResponse {

    private String productName;

    private double currentPrice;
    private Double avg7;
    private Double avg30;

    private Double rate7;
    private Double rate30;

    private Double dayChangePercent;
    private String risk;
}
