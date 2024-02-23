package com.scb.trade.enrichment.csv;

import java.io.Reader;

public interface CsvProcessor<T> {

    T processCsv(Reader reader);
}
