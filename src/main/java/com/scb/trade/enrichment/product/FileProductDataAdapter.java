package com.scb.trade.enrichment.product;

import com.scb.trade.enrichment.csv.CsvParser;
import com.scb.trade.enrichment.dto.Product;
import com.scb.trade.enrichment.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.scb.trade.enrichment.product.ProductConstants.*;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Slf4j
@Service
class FileProductDataAdapter implements ProductAdapter {
    private final ProductRepository productRepository;
    private final String productFile;
    private final ExecutorService executorService;

    FileProductDataAdapter(ProductRepository productRepository, @Value("${product.source.path}") String productFile) {
        this.productRepository = productRepository;
        this.productFile = productFile;
        this.executorService = newVirtualThreadPerTaskExecutor();
    }

    @PostConstruct
    void loadInMemoryProducts() {
        log.debug("Loading products from file: {}", productFile);
        var products = fetchProductData();
        log.debug("Loaded {} products", products.size());
        productRepository.saveAll(products);
        log.debug("Saved in memory {} products", products.size());
    }


    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @SneakyThrows
    @Override
    public Map<Long, Product> fetchProductData() {
        return executorService.submit(() -> {
            try (Reader reader = Files.newBufferedReader(Paths.get(productFile))) {
                return CsvParser.parse(reader, HEADER_NAMES,
                    (csvRecord, headers) ->
                        new Product(Long.parseLong(
                            csvRecord.get(headers.get(PRODUCT_ID))),
                            csvRecord.get(headers.get(PRODUCT_NAME))))
                    .collect(Collectors.toConcurrentMap(Product::id, Function.identity()));
            } catch (Exception e) {
                log.error("Unable to load product file {}", productFile);
                throw new IllegalArgumentException("Unable to load product file %s".formatted(productFile), e);
            }
        }).get(ofSeconds(10).toMillis(), MILLISECONDS);
    }

    @Override
    public String getProductNameById(Long id) {
        return productRepository.getProductById(id)
            .map(Product::name)
            .orElseGet(() -> {
                log.error("Missing product name for product_id %d".formatted(id));
                return DEFAULT_PRODUCT_NAME;
            });
    }
}
