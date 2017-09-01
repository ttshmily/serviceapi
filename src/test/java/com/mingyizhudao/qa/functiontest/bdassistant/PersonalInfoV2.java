package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dayi on 2017/6/23.
 */
public class PersonalInfoV2 extends BaseTest {
    public static final Logger logger= Logger.getLogger(PersonalInfoV2.class);
    public static String uri = "/api/v2/user/personal";
    public static String mock = false ? "/mockjs/1" : "";

    public static HashMap<String, List<String>> BDInfo(String token) {
        String res = "";
        res = HttpRequest.s_SendGet(host_bda + uri, "", token);
        HashMap<String, List<String>> result = new HashMap<>();
        JSONArray bd_city_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("city");
        List<String> cities = new ArrayList<>();
        for (int j=0; j<bd_city_list.size(); j++) {
            cities.add(bd_city_list.getJSONObject(j).getString("city_id"));
        }
        result.put("bd_city_list", cities);
        // TODO
        return result;
    }
    @Test
    public void test_01_没有token或token错误返回错误码(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");

        //错误的无效token
        res = HttpRequest.s_SendGet(host_bda + uri, query, "aaa");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");
    }

    @Test
    public void test_02_下属员工返回基本信息(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctorCounts"), "doctorCounts字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "orderCounts"), "orderCounts字段缺失");
        Assert.assertNull(Helper.s_ParseJson(data, "teamMemberCounts"));
        Assert.assertEquals(Helper.s_ParseJson(data, "role"), "1"); // 1-表示普通员工，2-表示主管
    }

    @Test
    public void test_03_员工主管返回基本信息和团队信息(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctorCounts"), "doctorCounts字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "orderCounts"), "orderCounts字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "teamMemberCounts"), "员工主管必须返回团队人员数量");
        Assert.assertEquals(Helper.s_ParseJson(data, "role"), "2"); // 1-表示普通员工，2-表示主管
    }

    @Test
    public void test_04_个人基本信息校验(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "user"), "user字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "staff_id"), "staff_id字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "userName"), "userName字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "city()"), "分配城市字段");
        Assert.assertNotNull(Helper.s_ParseJson(data, "role"), "role字段不能为空"); // 1-表示普通员工，2-表示主管
    }

    @Test
    public void test_05_个人负责城市信息的校验(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "city()"), "分配城市字段");

        JSONArray bd_city_list = JSONObject.fromObject(data).getJSONArray("city");
        for (int j=0; j<bd_city_list.size(); j++) {
            Assert.assertNotNull(bd_city_list.getJSONObject(j).getString("city_id"));
            Assert.assertNotNull(bd_city_list.getJSONObject(j).getString("city_name"));
        }
    }
}
