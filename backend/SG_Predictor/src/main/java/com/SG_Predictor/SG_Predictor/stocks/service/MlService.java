package com.SG_Predictor.SG_Predictor.stocks.service;

import com.SG_Predictor.SG_Predictor.config.WebClientConfig;
import com.SG_Predictor.SG_Predictor.stocks.dto.PredictReqDto;
import com.SG_Predictor.SG_Predictor.stocks.dto.PredictResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MlService {

    public final WebClientConfig webClientConfig;


    public PredictResDto predictRisk(PredictReqDto predictReqDto){
        try {
            log.info("Redirecting to FastAPI: POST /predict for symbol={} direction={} marketSession={}",
                predictReqDto.getSymbol(), predictReqDto.getDirection(), predictReqDto.getMarket_session());

            PredictResDto response = webClientConfig.mlWebClient().post().uri("/predict").bodyValue(predictReqDto).retrieve().bodyToMono(PredictResDto.class).block();

            log.info("Received response from FastAPI: decision={} loss_probability={}",
                response != null ? response.getDecision() : null,
                response != null ? response.getLoss_probability() : null);

            return response;
        } catch (WebClientResponseException exception) {
            log.error("FastAPI responded with status {} for /predict. Body: {}",
                exception.getStatusCode().value(), exception.getResponseBodyAsString(), exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "ML service returned " + exception.getStatusCode().value() + " for /predict");
        } catch (WebClientRequestException exception) {
            log.error("FastAPI unreachable for /predict: {}", exception.getMessage(), exception);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "ML service is unreachable. Check ml.service.base-url");
        }
    }
}
