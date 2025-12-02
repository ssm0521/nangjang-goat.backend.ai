package com.naengjang_goat.inventory_system.ai.service;

import com.naengjang_goat.inventory_system.ai.dto.OrderRecommendResponse;
import com.naengjang_goat.inventory_system.entity.Stock;
import com.naengjang_goat.inventory_system.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiOrderService {

    private final AiForecastService forecastService;
    private final StockRepository stockRepository;

    public OrderRecommendResponse recommend(String productName) {

        var forecast = forecastService.predictPrice(productName);

        Stock stock = stockRepository.findByProductName(productName);

        int currentStock = (stock == null) ? 0 : stock.getQuantity();

        int expectedSales = 20;

        int quantityToOrder = expectedSales - currentStock;
        boolean shouldOrder = quantityToOrder > 0;
        boolean priceWillIncrease = forecast.getPredictedPrice() > forecast.getAvg7();

        return new OrderRecommendResponse(
                productName,
                shouldOrder,
                Math.max(0, quantityToOrder),
                priceWillIncrease
        );
    }
}
