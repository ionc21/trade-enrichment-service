package com.scb.trade.enrichment.product;

import com.scb.trade.enrichment.dto.Product;
import com.scb.trade.enrichment.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
class InMemoryProductRepository implements ProductRepository {
    private final Map<Long, Product> productsMap = new ConcurrentHashMap<>();

    @Override
    public void save(Product product) {
        this.productsMap.put(product.id(), product);

    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productsMap.get(id));
    }

    @Override
    public void saveAll(Map<Long, Product> products) {
        productsMap.putAll(products);
    }
}
