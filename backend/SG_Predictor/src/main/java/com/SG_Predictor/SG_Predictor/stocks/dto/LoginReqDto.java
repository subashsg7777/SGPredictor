package com.SG_Predictor.SG_Predictor.stocks.dto;
 import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginReqDto {

    @NotBlank
    public String email;

    @NotBlank
    public String password;
}
