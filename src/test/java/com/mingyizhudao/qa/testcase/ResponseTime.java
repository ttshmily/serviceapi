package com.mingyizhudao.qa.testcase;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

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
//            System.out.println(postFilePath.split(":"));
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

//    private SampleResult results;
//    private String testStr;
//    //初始化方法
//    public void setupTest(JavaSamplerContext arg0) {
//        results = new SampleResult();
//        testStr = arg0.getParameter("testString", "");
//        if (testStr != null && testStr.length() > 0) {
//            results.setSamplerData(testStr);
//        }
//    }
//    //设置传入的参数
//    public Arguments getDefaultParameters() {
//        Arguments params = new Arguments();
//        params.addArgument("testStr", "");   //定义一个参数，显示到Jmeter的参数列表中，第一个参数为参数默认的显示名称，第二个参数为默认值
//        return params;
//    }
//    //测试执行的循环体，根据线程数和循环次数的不同可执行多次
//    public SampleResult runTest(JavaSamplerContext arg0) {
//        int len = 0;
//        results.sampleStart();
//        len = testStr.length();
//        results.sampleEnd();
//        if(len < 5){
//            System.out.println(testStr);
//            results.setSuccessful(false);   //用于设置运行结果的成功或失败，如果是"false"则表示结果失败，否则则表示成功
//        }else {
//            results.setSuccessful(true);
//        }
//        return results;
//    }
//    //结束方法
//    public void teardownTest(JavaSamplerContext arg0) {
//    }

}
