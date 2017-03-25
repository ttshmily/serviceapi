package com.mingyizhudao.qa.tc.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ttshmily on 23/3/2017.
 */
public class LoginTest {

    public static void main(String [] args) {
        String test="中国";
        try {
            System.out.print(URLEncoder.encode(test,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
