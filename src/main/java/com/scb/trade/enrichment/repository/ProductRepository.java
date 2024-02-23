package com.scb.trade.enrichment.repository;


import com.scb.trade.enrichment.dto.Product;

import java.util.Map;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product);
    Optional<Product> getProductById(Long id);

    void saveAll(Map<Long, Product> products);
}
