package com.SG_Predictor.SG_Predictor.stocks.dto;

// PredictResponse.java
import lombok.Data;

import java.util.List;
@Data
public class PredictResDto {
    private String decision;
    private double loss_probability;
    private List<Object> reasons;

    // getters & setters
}