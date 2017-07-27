package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class HospitalSearch extends BaseTest {

    public static final Logger logger= Logger.getLogger(HospitalSearch.class);
    public static String uri = "/api/hospitalsearch";
    public static String mock = false ? "/mockjs/1" : "";

    public static String hospitalSearch() {

        String res="";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"));
    }

    @Test
    public void test_02_没有searchName字段的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc+uri, "", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"));
    }

    @Test
    public void test_03_查询字符串为空时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","");
        try {
            res = HttpRequest.sendGet(host_doc+uri, map, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"));
    }

    @Test
    public void test_04_查询字符串为中文时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","人民医院");
        try {
            res = HttpRequest.sendGet(host_doc+uri, map, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"));
    }

    @Test
    public void test_05_查询字符串为一串拼音时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","changzhou");
        try {
            res = HttpRequest.sendGet(host_doc+uri, map, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"));
    }

    @Test
    public void test_06_查询字符串为中英混合时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","中国changzhou");
        try {
            res = HttpRequest.sendGet(host_doc+uri, map, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(Generator.parseJson(data, "hospital()"), "0");
    }

    @Test
    public void test_07_返回的结果中详细字段不缺少() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","安阳医院");
        try {
            res = HttpRequest.sendGet(host_doc+uri, map, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"), "hospital字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "hospital():name"), "hospital的name字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "hospital():id"), "hospital的id字段缺失");
//        Assert.assertNotNull(parseJson(data, "hospital():city"), "hospital的city字段缺失");
//        Assert.assertNotNull(parseJson(data, "hospital():ext"), "hospital的ext字段缺失");
//        Assert.assertNotNull(parseJson(data, "hospital():ext:surgery_list()"), "hospital的surgery字段为空");
    }

    @Test
    public void test_08_加城市ID搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String city_id = Generator.randomCityId();
        query.put("city_id", city_id);
        query.put("searchname","安阳医院");
        try {
            res = HttpRequest.sendGet(host_doc+uri, query, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "hospital()"), "hospital字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "hospital():name"), "hospital的name字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "hospital():id"), "hospital的id字段缺失");
        Assert.assertEquals(Generator.parseJson(data, "hospital():city_id"), city_id,"hospital的city字段缺失");
        Assert.assertEquals(Generator.parseJson(data, "hospital():city_name"), Generator.cityName(city_id), "hospital的city字段缺失");

    }

}
