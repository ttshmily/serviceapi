package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 9/4/2017.
 */
public class GetUploadToken extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getuploadtoken";

    @Test
    public void test_01_获取type1的图片token_成功() {

//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "当朝.jpg");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_获取type2的图片token_成功() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "2");
        query.put("filename", "abcd!@#$%^&*().jpg");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");

    }

    @Test
    public void test_03_获取type3的图片token_成功() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "3");
        query.put("filename", "abcd!@#$%^&*().jpg");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_04_获取PNG文件名正常图片token_成功() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.png");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcdefg.PNG");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_05_获取GIF文件名正常图片token_成功() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd.gif");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "abcd.GIF");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_06_获取文件名有特殊字符图片token_成功() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
////            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~.png");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("filename", "+_)(*&^%$#@!.PNG");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Ignore
    public void 获取文件名无后缀名图片token_失败() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "1");
        query.put("filename", "abcd!@#$%^&*(~aa");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
