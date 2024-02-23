package com.scb.trade.enrichment.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(OutputCaptureExtension.class)
class TradeEnrichmentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldEnrichTradeDataWithProductNameSkipMissing(CapturedOutput output) {

        // given
        MultipartBodyBuilder multipartBodyBuilder = loadResourceAsMultipartBodyBuilder("trade_data/trade.csv");
        String expectedResponseBody = loadResourceAsString("trade_data/response.csv");

        // when
        webTestClient.post()
            .uri("/api/v1/enrich")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(expectedResponseBody);

        // then
        assertThat(output).contains("Missing product name for product_id 11");
    }

    @Test
    void shouldEnrichTradeDataStreamWithProductNameSkipMissing(CapturedOutput output) {

        // given
        MultipartBodyBuilder multipartBodyBuilder = loadResourceAsMultipartBodyBuilder("trade_data/trade.csv");
        String expectedResponseBody = loadResourceAsString("trade_data/stream-response.csv");

        // when - then
        webTestClient.post()
            .uri("/api/v2/enrich")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(expectedResponseBody);
    }

    @Test
    void shouldSkipIncorrectDate(CapturedOutput output) {

        // given
        MultipartBodyBuilder multipartBodyBuilder = loadResourceAsMultipartBodyBuilder("invalid_date/trade.csv");
        String expectedResponseBody = loadResourceAsString("invalid_date/response.csv");

        // when
        webTestClient.post()
            .uri("/api/v1/enrich")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo(expectedResponseBody);


        assertThat(output).contains("Invalid date format: invalid date");
    }

    @Test
    void shouldReturnBadRequestForInvalidFile() {

        // given
        MultipartBodyBuilder multipartBodyBuilder = loadResourceAsMultipartBodyBuilder("invalid_file/trade.csv");

        // when-then
        webTestClient.post()
            .uri("/api/v1/enrich")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnInternalServerError() {

        // given
        MultipartBodyBuilder multipartBodyBuilder = loadResourceAsMultipartBodyBuilder("non_existing/trade.csv");

        // when-then
        webTestClient.post()
            .uri("/api/v1/enrich")
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().is5xxServerError();
    }

    private static MultipartBodyBuilder loadResourceAsMultipartBodyBuilder(String path) {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource(path))
            .contentType(MediaType.MULTIPART_FORM_DATA);
        return multipartBodyBuilder;
    }

    private static String loadResourceAsString(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
