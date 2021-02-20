package com.zostio.gthai;

import java.io.Serializable;

public class StatUtils implements Serializable {

    public static final int BUILD_NUMBER = 1;
    public static final String VERSION_CODE = "1.0.0";

    public static double getSigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static double randomDouble(double min, double max) {
        double a = Math.random();
        double num = min + Math.random() * (max - min);
        if(a < 0.5)
            return num;
        else
            return -num;
    }

    public static void printMessage(String message) {
        System.out.println(message);
    }
}
