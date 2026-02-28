package com.SG_Predictor.SG_Predictor.stocks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PredictReqDto {

    @NotBlank
    public String symbol;

    @NotBlank
    public String direction;

    @NotBlank
    public String market_session;

    @NotNull
    public double dist_from_high;

    @NotBlank
    public double dist_from_low;
}
