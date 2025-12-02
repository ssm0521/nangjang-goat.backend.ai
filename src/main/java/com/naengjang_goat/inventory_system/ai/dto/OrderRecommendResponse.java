package com.naengjang_goat.inventory_system.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRecommendResponse {
    private String productName;
    private boolean shouldOrder;  // 발주 필요 여부
    private int quantityToOrder;  // 주문해야 할 수량
    private boolean priceWillIncrease; // 가격 상승 예상 여부
}
