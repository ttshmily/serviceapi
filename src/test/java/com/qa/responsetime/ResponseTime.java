package com.qa.responsetime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by ttshmily on 7/5/2017.
 */
public class ResponseTime {

    public static void main(String[] args){
        try {
            String postFilePath = ResponseTime.class.getClassLoader().getResource(".").toString().split(":")[1] + "PostFile/login.txt";
            String cmd = "ab -n 10000 -c 1 -p " + postFilePath + " -T application/json http://login.dev.mingyizhudao.com/api/login/sendVerifyCode";
            System.out.println(cmd);
            Process pro = Runtime.getRuntime().exec(cmd);
            if (pro.waitFor() == 0 ) {
                InputStream in = pro.getInputStream();
                BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                String result = buf.readLine();
                while (result != null) {
                    System.out.println(result);
                    Pattern p = null;
                    result = buf.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
