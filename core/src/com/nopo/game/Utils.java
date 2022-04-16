package com.nopo.game;

public class Utils {
    //stole from https://www.demo2s.com/java/java-string-getline-string-s-int-line.html
    public static String getLine(String s, int line) {
        String[] lines = s.split("\r\n|\n|\r");
        if (lines.length >= line) {
            return lines[line - 1];
        } else {
            return null;
        }
    }

    public static int getLineLength(String s) {
        String[] lines = s.split("\r\n|\n|\r");
        return lines.length;
    }
}
