package com.SG_Predictor.SG_Predictor.stocks.service.impl;

import com.SG_Predictor.SG_Predictor.stocks.dto.LoginReqDto;
import com.SG_Predictor.SG_Predictor.stocks.dto.LoginResDto;

public interface UserImpl {

     LoginResDto loginUser(LoginReqDto loginReqDto);
}
