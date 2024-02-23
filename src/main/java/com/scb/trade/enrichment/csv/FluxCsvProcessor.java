package com.scb.trade.enrichment.csv;

import com.scb.trade.enrichment.dto.Trade;
import com.scb.trade.enrichment.product.ProductAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.scb.trade.enrichment.csv.CsvProcessorConstants.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class FluxCsvProcessor implements CsvProcessor<Flux<String>> {

    private final ProductAdapter productAdapter;

    @Override
    public Flux<String> processCsv(Reader reader) {
        Stream<Trade> trades = CsvParser.parse(reader, HEADER_NAMES,
            (csvRecord, headers) -> new Trade(csvRecord.get(headers.get(DATE)),
                Long.parseLong(csvRecord.get(headers.get(PRODUCT_ID))),
                csvRecord.get(headers.get(CURRENCY)),
                new BigDecimal(csvRecord.get(headers.get(PRICE)))));
        return Flux.concat(
            Flux.just("date,product_name,currency,price"),
            Flux.fromStream(trades)
                .filter(isValidTadeDate)
                .map(this::enrichTradeData));
    }

    private final Predicate<Trade> isValidTadeDate
        = trade -> {
        try {
            LocalDate.parse(trade.date(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: %s".formatted(trade.date()));
            return false;
        }
    };


    private String enrichTradeData(Trade trade) {
        String productName = productAdapter.getProductNameById(trade.productId());
        return System.lineSeparator() +
            trade.date() + "," +
            productName + "," +
            trade.currency() + "," +
            trade.price().stripTrailingZeros().toPlainString();
    }
}
