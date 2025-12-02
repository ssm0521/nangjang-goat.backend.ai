package com.naengjang_goat.inventory_system.ai.service;

import com.naengjang_goat.inventory_system.ai.dto.PriceAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiOrderService {

    private final AiForecastService forecastService;

    /**
     * 발주 추천 로직
     */
    public Map<String, Object> recommend(String productName) {

        // 분석 데이터 기반 추천
        PriceAnalysisResponse analysis = forecastService.analyzePrice(productName);

        Map<String, Object> result = new HashMap<>();

        // 기본 분석 정보
        result.put("productName", productName);
        result.put("currentPrice", analysis.getCurrentPrice());
        result.put("avg7", analysis.getAvg7());
        result.put("avg30", analysis.getAvg30());
        result.put("rate7", analysis.getRate7());
        result.put("rate30", analysis.getRate30());
        result.put("dayChangePercent", analysis.getDayChangePercent());
        result.put("risk", analysis.getRisk());

        // 추천 문구
        String recommendation;

        if ("HIGH".equals(analysis.getRisk())) {
            recommendation = "⚠️ 최근 가격 변동성이 큽니다. 최소 발주를 권장합니다.";
        } else if ("MID".equals(analysis.getRisk())) {
            recommendation = "📉 가격이 조금 불안정합니다. 적정량만 발주하세요.";
        } else {
            recommendation = "✅ 가격이 안정적입니다. 필요한 만큼 발주해도 좋습니다.";
        }

        result.put("recommendation", recommendation);

        return result;
    }
}
