package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.Generator;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Common_Cities extends BaseTest {

    public static final Logger logger= Logger.getLogger(Common_Cities.class);
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
        Assert.assertNotNull(Generator.parseJson(data, "list()"));
    }

    @Test
    public void test_02_查询城市列表_在指定省中查询城市() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        // city in province
        logger.info("在安徽省搜索城市关键字：安庆");
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
        logger.info("在非安徽省搜索城市关键字：安庆");
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
        String exp = "安庆";
        logger.info("搜索城市关键字："+exp);
        query.put("city_name", exp);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        Pattern p = Pattern.compile(exp);
        JSONArray cityList = data.getJSONArray("list");
        String cityName = cityList.getJSONObject(0).getString("name");
        Matcher m = p.matcher(cityName);
        Assert.assertTrue(m.find());


        exp = "宝";
        logger.info("搜索城市关键字："+exp);
        query.replace("city_name", exp);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        p = Pattern.compile(exp);
        cityList = data.getJSONArray("list");
//        for(int i=0; i<cityList.size(); i++) {
        for(int i=0; i<1; i++) { // 至少第一个有
                cityName = cityList.getJSONObject(i).getString("name");
                m = p.matcher(cityName);
                Assert.assertTrue(m.find());
        }

        exp = "上";
        logger.info("搜索城市关键字："+exp);
        query.replace("city_name", exp);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        p = Pattern.compile(exp);
        cityList = data.getJSONArray("list");
//        for(int i=0; i<cityList.size(); i++) {
        for(int i=0; i<1; i++) { // 至少第一个有
                cityName = cityList.getJSONObject(i).getString("name");
                m = p.matcher(cityName);
                Assert.assertTrue(m.find());
        }


    }

}
