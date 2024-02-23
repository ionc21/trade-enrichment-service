package com.scb.trade.enrichment.dto;

import java.math.BigDecimal;

public record Trade(String date, long productId, String currency, BigDecimal price) {
}
