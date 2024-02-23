package com.scb.trade.enrichment.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ProductConstants {

    static final String DEFAULT_PRODUCT_NAME = "Missing Product Name";
    static final String PRODUCT_ID = "product_id";
    static final String PRODUCT_NAME = "product_name";
    static final List<String> HEADER_NAMES = List.of(PRODUCT_ID, PRODUCT_NAME);
}
