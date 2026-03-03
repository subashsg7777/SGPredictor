package com.SG_Predictor.SG_Predictor.stocks.controller;

import com.SG_Predictor.SG_Predictor.stocks.dto.*;
import com.SG_Predictor.SG_Predictor.stocks.service.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PredictionController {

    private final UserService userService;
    private final MlService mlService;
    private final MarketContextBuilder marketContextBuilder;
    private final YahooMarketDataService yahooMarketDataService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginReqDto loginReqDto){

        LoginResDto result = userService.loginUser(loginReqDto);
        LoginResDto loginResDto = new LoginResDto();
        if(result.decision){
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        loginResDto.setMessage("Login Failed");
        loginResDto.setDecision(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginResDto);
    }

    @PostMapping("/predict")
    public PredictResDto riskPrediction(@Validated @NotBlank @RequestParam String symbol,@Validated @NotBlank @RequestParam String direction){
        log.info("Sending Traffic to Python Model");

        LocalTime tradeTime = LocalTime.now();

        StockFeatureData stockFeatureData = yahooMarketDataService.getFeatureData(symbol);
        MarketContext marketContext = marketContextBuilder.buildContext(stockFeatureData,tradeTime);

        PredictReqDto predictReqDto = new PredictReqDto();
        predictReqDto.setSymbol(symbol);
        predictReqDto.setDirection(direction);
        predictReqDto.setMarket_session(marketContext.getMarket_session());
        predictReqDto.setDist_from_high(marketContext.getDistFromHigh());
        predictReqDto.setDist_from_low(marketContext.getDistFromLow());

        return mlService.predictRisk(predictReqDto);
    }
}


