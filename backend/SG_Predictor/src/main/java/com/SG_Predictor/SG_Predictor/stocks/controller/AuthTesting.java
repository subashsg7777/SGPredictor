package com.SG_Predictor.SG_Predictor.stocks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthTesting {

    @GetMapping("testing")
    public String testing(){
        return "Auth Working";
    }
}
