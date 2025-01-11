package com.example.olxwebscrabber.util;

public class StringToNumberParser {

    public static Integer toInt(String text) {
        return Integer.valueOf(text.replaceAll("[^0-9]", ""));
    }

    public static Double toDouble(String text) {
        return Double.valueOf(text.replaceAll("[^0-9.]", ""));

    }
}
