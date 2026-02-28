package com.SG_Predictor.SG_Predictor.stocks.service;

import com.SG_Predictor.SG_Predictor.config.WebClientConfig;
import com.SG_Predictor.SG_Predictor.stocks.dto.PredictReqDto;
import com.SG_Predictor.SG_Predictor.stocks.dto.PredictResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MlService {

    public final WebClientConfig webClientConfig;


    public PredictResDto predictRisk(PredictReqDto predictReqDto){
        return webClientConfig.mlWebClient().post().uri("/predict").bodyValue(predictReqDto).retrieve().bodyToMono(PredictResDto.class).block();
    }
}
