package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dayi on 2017/6/22.
 */
public class TeamListV2 extends BaseTest {

    public static final Logger logger= Logger.getLogger(TeamListV2.class);
    public static String uri = "/api/v2/user/teamList";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_没有token调用应该失败() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, "");
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");
    }
    @Test
    public void test_02_token错误应该返回错误() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, "aaaaaaa");
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "错误token不应该调用成功");
    }
    @Test
    public void test_03_非主管token应该返回错误() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token_staff);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "非主管token不应该调用成功");
    }
    @Test
    public void test_04_排序规则_订单量() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("sortKey", "orderCounts");
        query.put("sortValue", "asc");
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray bd_list = data.getJSONArray("list");
        int orderCount = 0;
        int doctorCount = 0;
        int disactiveCount = 0;
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            int tmpOrderCount = Integer.parseInt(bd.getString("orderCounts"));
            Assert.assertTrue(orderCount <= tmpOrderCount);
            orderCount = tmpOrderCount;
        }

        query.replace("sortKey", "orderCounts");
        query.replace("sortValue", "desc");
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        bd_list = data.getJSONArray("list");
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            int tmpOrderCount = Integer.parseInt(bd.getString("orderCounts"));
            Assert.assertTrue(orderCount >= tmpOrderCount);
            orderCount = tmpOrderCount;
        }
    }
    @Test
    public void test_05_排序规则_未激活医生量() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("sortKey", "disActivationDoctorCount");
        query.put("sortValue", "asc");
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray bd_list = data.getJSONArray("list");
        int orderCount = 0;
        int doctorCount = 0;
        int disactiveCount = 0;
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            int tmpDisactiveCount = Integer.parseInt(bd.getString("disActivationDoctorCount"));
            Assert.assertTrue(disactiveCount <= tmpDisactiveCount);
            disactiveCount = tmpDisactiveCount;
        }

        query.replace("sortKey", "disActivationDoctorCount");
        query.replace("sortValue", "desc");
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        bd_list = data.getJSONArray("list");
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            int tmpDisactiveCount = Integer.parseInt(bd.getString("disActivationDoctorCount"));
            Assert.assertTrue(disactiveCount >= tmpDisactiveCount);
            disactiveCount = tmpDisactiveCount;
        }
    }
    @Test
    public void test_06_排序规则_医生量() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("sortKey", "doctorCounts");
        query.put("sortValue", "asc");
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray bd_list = data.getJSONArray("list");
        int orderCount = 0;
        int doctorCount = 0;
        int disactiveCount = 0;
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            int tmpDoctorCount = Integer.parseInt(bd.getString("doctorCounts"));
            Assert.assertTrue(doctorCount <= tmpDoctorCount);
            doctorCount = tmpDoctorCount;
        }

        query.replace("sortKey", "doctorCounts");
        query.replace("sortValue", "desc");
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        bd_list = data.getJSONArray("list");
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            int tmpDoctorCount = Integer.parseInt(bd.getString("doctorCounts"));
            Assert.assertTrue(doctorCount >= tmpDoctorCount);
            doctorCount = tmpDoctorCount;
        }
    }
    @Test
    public void test_07_默认情况下分页数据() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Generator.parseJson(data, "list()"), "地推人员列表字段不能缺失");
        Assert.assertNotNull(Generator.parseJson(data, "page"), "页码字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "page_size"), "分页大小字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "size"), "列表总数字段缺失");
    }

    @Test
    public void test_08_根据城市ID筛选() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String city = Generator.randomCityId();
        query.put("city_id", city);
        try {
            res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray bd_list = data.getJSONArray("list");
        for(int i=0; i<bd_list.size(); i++) {
            JSONObject bd = bd_list.getJSONObject(i);
            JSONArray bd_city_list = new JSONArray();
            if (!bd.getString("city").equals("null")) {
                bd_city_list = bd.getJSONArray("city");
            }
            List<String> cities = new ArrayList<>();
            for (int j=0; j<bd_city_list.size(); j++) {
                cities.add(bd_city_list.getJSONObject(j).getString("city_id"));
            }
            Assert.assertTrue(cities.contains(city));
        }
    }

}
