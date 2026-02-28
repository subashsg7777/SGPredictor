package com.SG_Predictor.SG_Predictor.stocks;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
public class YahooFinanceService {

    private static final Pattern PRICE_PATTERN = Pattern.compile("\\\"regularMarketPrice\\\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("\\\"currency\\\"\\s*:\\s*\\\"([A-Z]{3})\\\"");
    private static final Duration CACHE_TTL = Duration.ofSeconds(30);

    private final RestClient restClient;
    private final Map<String, CachedQuote> quoteCache = new ConcurrentHashMap<>();

    public YahooFinanceService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://query1.finance.yahoo.com").build();
    }

    public Quote fetchQuote(String symbol) {
        CachedQuote cachedQuote = quoteCache.get(symbol);
        if (cachedQuote != null && cachedQuote.isFresh()) {
            return cachedQuote.quote();
        }

        try {
            Quote quote = fetchFromQuoteEndpoint(symbol);
            quoteCache.put(symbol, new CachedQuote(quote, Instant.now()));
            return quote;
        } catch (IllegalStateException primaryError) {
            try {
                Quote chartQuote = fetchFromChartEndpoint(symbol);
                quoteCache.put(symbol, new CachedQuote(chartQuote, Instant.now()));
                return chartQuote;
            } catch (IllegalStateException secondaryError) {
                if (cachedQuote != null) {
                    return cachedQuote.quote();
                }
                throw new IllegalStateException(secondaryError.getMessage() != null
                        ? secondaryError.getMessage()
                        : primaryError.getMessage());
            }
        }
    }

    private Quote fetchFromQuoteEndpoint(String symbol) {
        String payload = fetchYahooPayload("/v7/finance/quote", symbol);
        if (!payload.contains("\"result\":[") || payload.contains("\"result\":[]")) {
            throw new IllegalStateException("No quote found for symbol: " + symbol);
        }
        return parseQuoteFromPayload(symbol, payload);
    }

    private Quote fetchFromChartEndpoint(String symbol) {
        String payload = fetchYahooPayload("/v8/finance/chart", symbol);
        if (!payload.contains("\"chart\"")) {
            throw new IllegalStateException("No chart found for symbol: " + symbol);
        }
        return parseQuoteFromPayload(symbol, payload);
    }

    private String fetchYahooPayload(String endpointPath, String symbol) {
        String payload;
        try {
            payload = restClient.get()
                    .uri(uriBuilder -> {
                        if ("/v7/finance/quote".equals(endpointPath)) {
                            return uriBuilder.path(endpointPath)
                                    .queryParam("symbols", symbol)
                                    .build();
                        }
                        return uriBuilder.path(endpointPath + "/{symbol}")
                                .queryParam("range", "1d")
                                .queryParam("interval", "1m")
                                .build(symbol);
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Referer", "https://finance.yahoo.com")
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException exception) {
            int statusCode = exception.getStatusCode().value();
            throw new IllegalStateException(statusCode + " " + exception.getStatusText());
        }

        if (payload == null || payload.isBlank()) {
            throw new IllegalStateException("Empty response from quote provider");
        }
        return payload;
    }

    private Quote parseQuoteFromPayload(String symbol, String payload) {
        Matcher priceMatcher = PRICE_PATTERN.matcher(payload);
        if (!priceMatcher.find()) {
            throw new IllegalStateException("LTP is unavailable for symbol: " + symbol);
        }

        BigDecimal price = new BigDecimal(priceMatcher.group(1));

        Matcher currencyMatcher = CURRENCY_PATTERN.matcher(payload);
        String currency = currencyMatcher.find() ? currencyMatcher.group(1) : "INR";

        return new Quote(symbol, price, currency);
    }

    private record CachedQuote(Quote quote, Instant fetchedAt) {
        boolean isFresh() {
            return fetchedAt.plus(CACHE_TTL).isAfter(Instant.now());
        }
    }

    public record Quote(String symbol, BigDecimal price, String currency) {
    }
}
