package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Common_SearchHospitals extends BaseTest {

    public static final Logger logger= Logger.getLogger(Common_SearchHospitals.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/hospitals/search";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_查询默认医院列表() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("hospital_name", "安庆");

        try {
            res = HttpRequest.sendGet(host_crm + uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_查询医院列表_根据查询条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        // 查询字符串中文
        query.put("hospital_name", "安阳");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000"); // 必须要有输入
        Assert.assertNotEquals(Generator.parseJson(data,"list()"), "0");
    }

    @Test
    public void test_03_查询医院列表_根据查询条件_拼音() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        // 查询字符串为拼音
        query.put("hospital_name", "anyang");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotEquals(Generator.parseJson(data,"list()"), "0");
    }

    @Test
    public void test_04_查询医院列表_根据查询条件_中英混合() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        // 查询字符串为中文拼音混合
        query.put("hospital_name", "安阳yiyuan");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotEquals(Generator.parseJson(data, "list()"), "0", "搜索结果不应该为0");
    }

    @Test(enabled = false)
    public void test_05_查询医院列表_根据查询条件_无key() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        // 查询key不存在
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
