package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class SearchCities extends BaseTest {

    public static final Logger logger= Logger.getLogger(SearchCities.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/cities";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_查询默认城市列表_默认返回热门城市() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        Assert.assertEquals(parseJson(data, ""), "北京");
    }

    @Test
    public void test_02_查询城市列表_在指定省中查询城市() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        // city in province
        query.put("province_id", "340000"); // 安徽
        query.put("city_name", "安庆");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        // city not in province
        query.replace("province_id", "330000");
        query.replace("city_name", "安庆");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_03_查询城市列表_在全国范围查询城市() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        // city in province
        query.put("city_name", "安庆");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        query.replace("city_name", "宝鸡");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

}
