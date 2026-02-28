package com.SG_Predictor.SG_Predictor.utill;

import java.time.LocalTime;

public class SessionUtill {

    public static String resolve(LocalTime time) {

        if (time.isBefore(LocalTime.of(10, 30))) {
            return "EARLY";
        } else if (time.isBefore(LocalTime.of(14, 30))) {
            return "MID";
        } else {
            return "CLOSE";
        }
    }

}
