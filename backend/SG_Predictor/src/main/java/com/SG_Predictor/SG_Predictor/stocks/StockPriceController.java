package com.SG_Predictor.SG_Predictor.stocks;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {

    private static final List<String> DEFAULT_SYMBOLS = List.of(
            "RELIANCE.NS",
            "TCS.NS",
            "INFY.NS",
            "HDFCBANK.NS",
            "ICICIBANK.NS",
            "SBIN.NS",
            "LT.NS",
            "ITC.NS",
            "BHARTIARTL.NS",
            "KOTAKBANK.NS");

    private final YahooFinanceService yahooFinanceService;

    public StockPriceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/symbols")
    public ResponseEntity<?> getSymbols() {
        return ResponseEntity.ok(new SymbolsResponse(DEFAULT_SYMBOLS));
    }

    @GetMapping("/price")
    public ResponseEntity<?> getPrice(@RequestParam("symbol") String symbol) {
        String normalizedSymbol = symbol == null ? "" : symbol.trim().toUpperCase();
        if (normalizedSymbol.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Query parameter 'symbol' is required"));
        }

        try {
            YahooFinanceService.Quote quote = yahooFinanceService.fetchQuote(normalizedSymbol);
            return ResponseEntity.ok(new StockPriceResponse(
                    quote.symbol(),
                    quote.price(),
                    quote.currency(),
                    "yfinance",
                    Instant.now()));
        } catch (IllegalStateException exception) {
            HttpStatus status = exception.getMessage() != null && exception.getMessage().contains("Too Many Requests")
                    ? HttpStatus.TOO_MANY_REQUESTS
                    : HttpStatus.BAD_GATEWAY;
            return ResponseEntity.status(status).body(new ErrorResponse(exception.getMessage()));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse("Unable to fetch live price"));
        }
    }

    public record SymbolsResponse(List<String> symbols) {
    }

    public record StockPriceResponse(
            String symbol,
            java.math.BigDecimal price,
            String currency,
            String source,
            Instant fetchedAt) {
    }

    public record ErrorResponse(String error) {
    }
}
