package com.SG_Predictor.SG_Predictor.stocks.service;

import com.SG_Predictor.SG_Predictor.config.WebClientConfig;
import com.SG_Predictor.SG_Predictor.stocks.dto.PredictReqDto;
import com.SG_Predictor.SG_Predictor.stocks.dto.PredictResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MlService {

    public final WebClientConfig webClientConfig;


    public PredictResDto predictRisk(PredictReqDto predictReqDto){
        try {
            return webClientConfig.mlWebClient().post().uri("/predict").bodyValue(predictReqDto).retrieve().bodyToMono(PredictResDto.class).block();
        } catch (WebClientResponseException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "ML service returned " + exception.getStatusCode().value() + " for /predict");
        } catch (WebClientRequestException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "ML service is unreachable. Check ml.service.base-url");
        }
    }
}
