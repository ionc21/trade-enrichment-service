package com.scb.trade.enrichment.csv;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class CsvProcessorConstants {

   public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

   public static final String DATE = "date";
   public static final String PRODUCT_ID = "product_id";
   public static final String CURRENCY = "currency";
   public static final String PRICE = "price";
   public static final List<String> HEADER_NAMES = List.of(DATE, PRODUCT_ID, CURRENCY, PRICE);
}
