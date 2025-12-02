package com.naengjang_goat.inventory_system.ai.service;

import com.naengjang_goat.inventory_system.ai.dto.PriceAnalysisResponse;
import com.naengjang_goat.inventory_system.analysis.domain.PriceHistory;
import com.naengjang_goat.inventory_system.analysis.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiForecastService {

    private final PriceHistoryRepository priceHistoryRepository;

    /**
     * 가격 분석: 7일/30일 평균 + 변화율 + 위험도
     */
    public PriceAnalysisResponse analyzePrice(String productName) {

        List<PriceHistory> historyList =
                priceHistoryRepository.findTop30ByProductNameOrderByPriceDateDesc(productName);

        if (historyList.isEmpty()) {
            return PriceAnalysisResponse.builder()
                    .productName(productName)
                    .currentPrice(0)
                    .avg7(null)
                    .avg30(null)
                    .rate7(null)
                    .rate30(null)
                    .dayChangePercent(null)
                    .risk("LOW")
                    .build();
        }

        // ▢ 오늘 가격
        double currentPrice = parsePrice(historyList.get(0).getRetailPrice());

        // ▢ 최근 7일 평균
        int size7 = Math.min(7, historyList.size());
        double sum7 = 0;
        for (int i = 0; i < size7; i++) {
            sum7 += parsePrice(historyList.get(i).getRetailPrice());
        }
        Double avg7 = size7 > 0 ? sum7 / size7 : null;

        // ▢ 최근 30일 평균
        int size30 = historyList.size();
        double sum30 = 0;
        for (int i = 0; i < size30; i++) {
            sum30 += parsePrice(historyList.get(i).getRetailPrice());
        }
        Double avg30 = size30 > 0 ? sum30 / size30 : null;

        // ▢ 평균 대비 변화율
        Double rate7 = (avg7 != null && avg7 != 0)
                ? (currentPrice - avg7) / avg7 * 100.0 : null;

        Double rate30 = (avg30 != null && avg30 != 0)
                ? (currentPrice - avg30) / avg30 * 100.0 : null;

        // ▢ 어제 대비 변동률
        Double dayChangePercent = null;
        if (historyList.size() >= 2) {
            double yesterdayPrice = parsePrice(historyList.get(1).getRetailPrice());
            if (yesterdayPrice != 0) {
                dayChangePercent = (currentPrice - yesterdayPrice) / yesterdayPrice * 100.0;
            }
        }

        // ▢ 위험도 계산
        String risk = calculateRisk(dayChangePercent);

        return PriceAnalysisResponse.builder()
                .productName(productName)
                .currentPrice(currentPrice)
                .avg7(avg7)
                .avg30(avg30)
                .rate7(rate7)
                .rate30(rate30)
                .dayChangePercent(dayChangePercent)
                .risk(risk)
                .build();
    }

    /**
     * 예측 + 그래프용 데이터 생성
     * 최근 30일 트렌드를 기반으로 향후 7일 가격을 선형 예측
     */
    public Map<String, Object> forecastGraph(String productName) {

        List<PriceHistory> historyList =
                priceHistoryRepository.findTop30ByProductNameOrderByPriceDateDesc(productName);

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> historyData = new ArrayList<>();
        List<Map<String, Object>> predictionData = new ArrayList<>();

        if (historyList.isEmpty()) {
            result.put("productName", productName);
            result.put("history", historyData);
            result.put("prediction", predictionData);
            return result;
        }

        // ------------------------------
        // 1) 과거 히스토리 데이터(history)
        // ------------------------------
        for (PriceHistory ph : historyList) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", ph.getPriceDate().toString());
            item.put("price", parsePrice(ph.getRetailPrice()));
            historyData.add(item);
        }

        // 현재 가격 = 가장 최신 가격
        double currentPrice = parsePrice(historyList.get(0).getRetailPrice());

        // ------------------------------
        // 2) 선형 기울기 계산 (예측용)
        // ------------------------------
        double slope = 0;
        if (historyList.size() >= 30) {
            double price30DaysAgo =
                    parsePrice(historyList.get(historyList.size() - 1).getRetailPrice());
            slope = (currentPrice - price30DaysAgo) / 30.0;
        }

        // ------------------------------
        // 3) 향후 7일 예측(prediction)
        // ------------------------------
        LocalDate lastDate = historyList.get(0).getPriceDate();

        for (int i = 1; i <= 7; i++) {
            Map<String, Object> pred = new HashMap<>();
            LocalDate date = lastDate.plusDays(i);

            double predictedPrice = currentPrice + slope * i;

            pred.put("date", date.toString());
            pred.put("price", predictedPrice);

            predictionData.add(pred);
        }

        // ------------------------------
        // 4) 최종 응답 구성
        // ------------------------------
        result.put("productName", productName);
        result.put("currentPrice", currentPrice);
        result.put("history", historyData);       // 과거 30일
        result.put("prediction", predictionData); // 향후 7일 예측

        return result;
    }

    /**
     * 문자열 가격 → double 변환 (오류 방지)
     */
    private double parsePrice(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        try {
            return Double.parseDouble(raw);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 변동률 기반 위험도 계산
     */
    private String calculateRisk(Double pct) {
        if (pct == null) return "LOW";

        double abs = Math.abs(pct);

        if (abs >= 10) return "HIGH";
        if (abs >= 3) return "MID";
        return "LOW";
    }
}
