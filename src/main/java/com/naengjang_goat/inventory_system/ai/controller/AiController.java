package com.naengjang_goat.inventory_system.ai.controller;

import com.naengjang_goat.inventory_system.ai.dto.ForecastResponse;
import com.naengjang_goat.inventory_system.ai.dto.OrderRecommendResponse;
import com.naengjang_goat.inventory_system.ai.service.AiForecastService;
import com.naengjang_goat.inventory_system.ai.service.AiOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiForecastService aiForecastService;
    private final AiOrderService aiOrderService;

    @GetMapping("/forecast")
    public ForecastResponse forecast(@RequestParam String productName) {
        return aiForecastService.predictPrice(productName);
    }

    @GetMapping("/order-recommend")
    public OrderRecommendResponse recommend(@RequestParam String productName) {
        return aiOrderService.recommend(productName);
    }
}
