package com.naengjang_goat.inventory_system.ai.controller;

import com.naengjang_goat.inventory_system.ai.dto.PriceAnalysisResponse;
import com.naengjang_goat.inventory_system.ai.service.AiForecastService;
import com.naengjang_goat.inventory_system.ai.service.AiOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiForecastService aiForecastService;
    private final AiOrderService aiOrderService;

    // 기존 추천 API
    @GetMapping("/recommend")
    public Map<String, Object> recommend(@RequestParam String productName) {
        return aiOrderService.recommend(productName);
    }

    // 가격 분석 API
    @GetMapping("/price-analysis")
    public PriceAnalysisResponse analyzePrice(@RequestParam String productName) {
        return aiForecastService.analyzePrice(productName);
    }

    // 그래프 + 예측 API
    @GetMapping("/forecast-graph")
    public Map<String, Object> forecastGraph(@RequestParam String productName) {
        return aiForecastService.forecastGraph(productName);
    }
}
