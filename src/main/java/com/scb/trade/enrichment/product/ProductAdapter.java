package com.scb.trade.enrichment.product;

import com.scb.trade.enrichment.dto.Product;

import java.util.Map;

public interface ProductAdapter {

    String getProductNameById(Long id);

    Map<Long, Product> fetchProductData();
}
