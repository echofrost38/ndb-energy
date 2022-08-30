package com.ndb.auction.utils;

public class Utilities {
    
    // locktime format
    public static String lockTimeFormat(int seconds) {
        int sec = seconds % 60;
        int min = Math.floorDiv((seconds % 3600) / 60, Integer.MIN_VALUE);
        int hours = Math.floorDiv(seconds / 3600, Integer.MIN_VALUE);
        String formatted = "";
        if(hours > 0) formatted += String.format("%dhr ", hours);
        if(min > 0) formatted += String.format("%dm ", min);
        formatted += String.format("%ds", sec);
        return formatted;
    }
    
}
