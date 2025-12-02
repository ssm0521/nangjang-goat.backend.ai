package com.naengjang_goat.inventory_system.repository;

import com.naengjang_goat.inventory_system.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByProductName(String productName);
}
