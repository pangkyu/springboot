package com.example.chapter6.Util;

import java.util.Random;

public class Util {

//    strLength 길이의 랜덤 문자열 생성
    public static String generateRandomString(int strLength){
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder(strLength);

        for(int i=0; i<strLength; i++){
            char tmp = (char) ('a' + random.nextInt('z'-'a'));
            stringBuilder.append(tmp);
        }

        return stringBuilder.toString();
    }
}
