package com.zostio.myai;

public class StatUtils {
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
