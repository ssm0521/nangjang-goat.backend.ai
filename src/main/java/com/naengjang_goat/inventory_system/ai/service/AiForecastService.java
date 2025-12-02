package com.naengjang_goat.inventory_system.ai.service;

import com.naengjang_goat.inventory_system.ai.dto.ForecastResponse;
import com.naengjang_goat.inventory_system.analysis.domain.PriceHistory;
import com.naengjang_goat.inventory_system.analysis.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiForecastService {

    private final PriceHistoryRepository priceHistoryRepository;

    public ForecastResponse predictPrice(String productName) {

        // PriceHistoryRepository에 findByProductName이 없다!
        // → 그래서 전체 가져와서 필터링해야 함
        List<PriceHistory> all = priceHistoryRepository.findAll();

        // product_name으로 필터
        List<PriceHistory> prices = all.stream()
                .filter(p -> p.getProductName().equals(productName))
                .sorted(Comparator.comparing(PriceHistory::getPriceDate).reversed())
                .limit(30)
                .toList();

        if (prices.isEmpty()) {
            return new ForecastResponse(productName, 0, 0, 0);
        }

        double avg7 = movingAverage(prices, 7);
        double avg30 = movingAverage(prices, 30);
        double trend = linearTrend(prices);

        double predicted = avg7 + trend;

        return new ForecastResponse(productName, predicted, avg7, avg30);
    }

    private double movingAverage(List<PriceHistory> list, int days) {
        int count = Math.min(list.size(), days);
        double sum = 0;

        for (int i = 0; i < count; i++) {
            String price = list.get(i).getRetailPrice();
            if (price == null) continue;
            sum += Double.parseDouble(price);
        }

        return sum / count;
    }

    private double linearTrend(List<PriceHistory> prices) {
        if (prices.size() < 2) return 0;

        double first = Double.parseDouble(prices.get(prices.size() - 1).getRetailPrice());
        double last = Double.parseDouble(prices.get(0).getRetailPrice());

        return (last - first) / prices.size();
    }
}
