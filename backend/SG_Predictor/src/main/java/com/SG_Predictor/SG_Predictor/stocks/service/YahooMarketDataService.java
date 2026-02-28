package com.SG_Predictor.SG_Predictor.stocks.service;

import com.SG_Predictor.SG_Predictor.stocks.dto.StockFeatureData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class YahooMarketDataService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public YahooMarketDataService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://query1.finance.yahoo.com")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();
    }

    public StockFeatureData getFeatureData(String symbol) {

        try {
            // 1️⃣ Fetch RAW JSON as String
            String rawJson = webClient.get()
                    .uri("/v8/finance/chart/{symbol}", symbol)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 2️⃣ Parse JSON safely
            JsonNode root = objectMapper.readTree(rawJson);

            JsonNode meta = root
                    .path("chart")
                    .path("result")
                    .get(0)
                    .path("meta");

            if (meta.isMissingNode() || meta.isNull()) {
                throw new RuntimeException("Invalid symbol or empty market data");
            }

            return new StockFeatureData(
                    meta.path("symbol").asText(),
                    meta.path("regularMarketPrice").asDouble(),
                    meta.path("regularMarketDayHigh").asDouble(),
                    meta.path("regularMarketDayLow").asDouble()
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Yahoo market data", e);
        }
    }
}