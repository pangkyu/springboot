package com.example.chapter6.util;

import java.util.Random;

public class Util {
    public static String generateRandomString(int strLength){
        Random random = new Random();
        String result = "";
        StringBuilder stringBuilder = new StringBuilder(strLength);
        for ( int i = 0; i < strLength; i++){
            char tmp = (char) ('a' + random.nextInt('z' - 'a'));
            stringBuilder.append(tmp);
        }
        return stringBuilder.toString();
    }
}
