package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class HospitalSearch extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/hospitalsearch";

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
//        String userToken = "";
//        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
//        if(mainDoctorInfo == null) {
//            logger.error("创建注册专家失败，退出执行");
//            System.exit(10000);
//        }
//        userToken = mainDoctorInfo.get("token");

        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"));
    }

    @Test
    public void test_02_没有searchName字段的请求可以获得有效信息() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri, "", "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"));
    }

    @Test
    public void test_03_查询字符串为空时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","");
        res = HttpRequest.s_SendGet(host_doc + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"));
    }

    @Test
    public void test_04_查询字符串为中文时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","人民医院");
        res = HttpRequest.s_SendGet(host_doc + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"));
    }

    @Test
    public void test_05_查询字符串为一串拼音时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","changzhou");
        res = HttpRequest.s_SendGet(host_doc + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"));
    }

    @Test
    public void test_06_查询字符串为中英混合时的返回结果() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","中国changzhou");
        res = HttpRequest.s_SendGet(host_doc + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data, "hospital()"), "0");
    }

    @Test
    public void test_07_返回的结果中详细字段不缺少() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("searchname","安阳医院");
        res = HttpRequest.s_SendGet(host_doc + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"), "hospital字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital():name"), "hospital的name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital():id"), "hospital的id字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "hospital():city"), "hospital的city字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "hospital():ext"), "hospital的ext字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "hospital():ext:surgery_list()"), "hospital的surgery字段为空");
    }

    @Test
    public void test_08_加城市ID搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String city_id = Generator.randomCityId();
        query.put("city_id", city_id);
        query.put("searchname","安阳医院");
        res = HttpRequest.s_SendGet(host_doc + uri, query, "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital()"), "hospital字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital():name"), "hospital的name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital():id"), "hospital的id字段缺失");
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital():city_id"), city_id,"hospital的city字段缺失");
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital():city_name"), Generator.cityName(city_id), "hospital的city字段缺失");
    }
}
