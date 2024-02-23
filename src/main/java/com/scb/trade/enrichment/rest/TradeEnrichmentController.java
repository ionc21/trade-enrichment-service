package com.scb.trade.enrichment.rest;

import com.scb.trade.enrichment.csv.CsvProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TradeEnrichmentController {

    private final CsvProcessor<Flux<String>> processor;

    @PostMapping(value = "/v1/enrich", consumes = "multipart/form-data", produces = "text/csv")
    public Flux<String> enrichTradeData(@RequestPart("file") FilePart filePart) {
        return filePart.content()
            .map(dataBuffer -> new InputStreamReader(dataBuffer.asInputStream(), StandardCharsets.UTF_8))
            .flatMap(processor::processCsv);
    }

    @PostMapping(value = "/v2/enrich", consumes = "multipart/form-data", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> enrichTradeStream(@RequestPart("file") FilePart filePart) {
        return filePart.content()
            .map(dataBuffer -> new InputStreamReader(dataBuffer.asInputStream(), StandardCharsets.UTF_8))
            .flatMap(processor::processCsv);
    }
}
