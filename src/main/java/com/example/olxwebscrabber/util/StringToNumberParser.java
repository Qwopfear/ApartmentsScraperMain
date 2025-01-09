package com.example.olxwebscrabber.util;

public class StringToIntParser {

    public static Integer toInt(String text) {
        return Integer.valueOf(text.replaceAll("[^0-9]", ""));
    }
}
