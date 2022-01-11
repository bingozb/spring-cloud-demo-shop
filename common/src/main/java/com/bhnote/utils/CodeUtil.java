package com.bhnote.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author bingo
 * @date 2022/1/6
 */
public class CodeUtil {

    /**
     * 生成随机数字验证码
     */
    public static String randomNumberCode(int length) {
        String sourceString = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(sourceString.charAt(random.nextInt(9)));
        }
        return sb.toString();
    }

    /**
     * 生成随机字母和数字
     */
    public static String randomNumberAndLetter(int length) {
        final String allCharNum = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder saltString = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            saltString.append(allCharNum.charAt(random.nextInt(allCharNum.length())));
        }
        return saltString.toString();
    }

    /**
     * 生成UUID（32位）
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
}
