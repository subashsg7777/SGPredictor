package com.SG_Predictor.SG_Predictor.stocks.service;

import com.SG_Predictor.SG_Predictor.stocks.dto.StockFeatureData;
import com.SG_Predictor.SG_Predictor.utill.SessionUtill;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class MarketContextBuilder {

    public MarketContext buildContext(StockFeatureData stockFeatureData, LocalTime tradeTime){

        double ltp = stockFeatureData.getLastPrice();
        double high = stockFeatureData.getDayHigh();
        double low = stockFeatureData.getDayLow();

        double range = high - low;
         double dist_from_high = (high - ltp) / range;
         double dist_from_low = (ltp - low) / range;

         String session = SessionUtill.resolve(tradeTime);

         return new MarketContext(dist_from_high,dist_from_low,session);
    }
}
