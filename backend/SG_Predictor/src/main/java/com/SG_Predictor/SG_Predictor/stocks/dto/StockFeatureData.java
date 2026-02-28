package com.SG_Predictor.SG_Predictor.stocks.dto;

import lombok.Data;

@Data
public class StockFeatureData {

    private final String symbol;
    private final double lastPrice;
    private final double dayHigh;
    private final double dayLow;

    public StockFeatureData(String symbol, double lastPrice, double dayHigh, double dayLow) {
        this.symbol = symbol;
        this.lastPrice = lastPrice;
        this.dayHigh = dayHigh;
        this.dayLow = dayLow;
    }
}