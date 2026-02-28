package com.SG_Predictor.SG_Predictor.stocks.service;

import lombok.Data;

@Data
public class MarketContext {
    public double distFromHigh;
    public double distFromLow;
    public String market_session;

    public MarketContext(double distFromHigh, double distFromLow) {
        this.distFromHigh = distFromHigh;
        this.distFromLow = distFromLow;

    }

    public MarketContext(double distFromHigh, double distFromLow,String market_session) {
        this.distFromHigh = distFromHigh;
        this.distFromLow = distFromLow;
        this.market_session = market_session;
    }
}