package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 17/5/2017.
 */
public class SearchHospital extends BaseTest {

    public static final Logger logger= Logger.getLogger(SearchHospital.class);
    public static String uri = "/api/v1/hospitals/search";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_返回的结果中详细字段不缺少() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        logger.info("搜索字段：安阳医院");
        map.put("hospital_name","安阳医院");
        res = HttpRequest.s_SendGet(host_bda + uri, map, "", null);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0)"), "hospital字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):name"), "hospital的name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):id"), "hospital的id字段缺失");
    }

    @Test(enabled = false)
    public void test_02_没有hospital_name字段的请求可以获得有效信息() {
        String res = "";
        res = HttpRequest.s_SendGet(host_bda + uri, "", "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"));
    }

    @Test(enabled = false)
    public void test_03_查询字符串为空时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("hospital_name","");
        res = HttpRequest.s_SendGet(host_bda + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"));
    }

    @Test
    public void test_04_查询字符串为中文时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("hospital_name","人民医院");
        res = HttpRequest.s_SendGet(host_bda + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"));
    }

    @Test
    public void test_05_查询字符串为一串拼音时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("hospital_name","changzhou");
        res = HttpRequest.s_SendGet(host_bda + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"));
    }

    @Test
    public void test_06_查询字符串为中英混合时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("hospital_name","中国changzhou");
        res = HttpRequest.s_SendGet(host_bda + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list()"), "0");
    }

}
