package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.Generator;
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
 * Created by ttshmily on 17/5/2017.
 */
public class OrderList extends BaseTest {

    public static final Logger logger= Logger.getLogger(OrderList.class);
    public static String uri = "/api/v1/order/orderList";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_未登录用户无权限使用接口() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, map, "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");
    }

    @Test
    public void test_02_登录用户_不传入地推ID获取个人订单列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", Generator.randomEmployeeId());
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):agent_name"), "下级医生姓名字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):id"), "订单编号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):major_disease_name"), "主诉疾病字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "list(0):minor_disease_name"), "次诉疾病字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "list(0):major_reps_name"), "客服姓名字段缺失");
    }

    @Test
    public void test_03_登录用户_传入自己的地推ID获取个人订单列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0133");
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):agent_name"), "下级医生姓名字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):id"), "订单编号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):major_disease_name"), "主诉疾病字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "list(0):minor_disease_name"), "次诉疾病字段缺失");
//        Assert.assertNotNull(s_ParseJson(data, "list(0):major_reps_name"), "客服姓名字段缺失");
    }

    @Test
    public void test_04_登录用户_传入下属地推ID获取其订单列表() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        query.put("agent_contact_id", agent_contact_id);
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):agent_name"), "下级医生姓名字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):id"), "订单编号字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):major_disease_name"), "主诉疾病字段缺失");
    }

    @Test
    public void test_05_登录用户_传入非下属地推ID无法获取其订单列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0001");
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "传入非下属ID不应该获取成功");
    }

    public void test_06_输入检查_分页逻辑() {

    }

    @Test
    public void test_07_输入检查_过滤订单状态() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        map.put("agent_contact_id", agent_contact_id);
        map.put("status", "1000");
        List<String> status = new ArrayList<>();
        status.add("1000");
        status.add("2000");
        status.add("2010");
        status.add("2020");
        status.add("3000");
        status.add("4000");
        status.add("4010");
        status.add("4020");
        status.add("5000");

        for (String s:status
                ) {
            map.replace("status", s);
            res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000", "有token应该调用成功");
            JSONArray orderList = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < orderList.size(); i++) {
                JSONObject ord = orderList.getJSONObject(i);
                Assert.assertEquals(Helper.s_ParseJson(ord, "status"), s, "过滤后有杂质状态存在");
            }
        }
    }

    public void test_08_输入检查_过滤排序逻辑() {

    }

    public void test_09_输入检查_过滤医生ID() {

    }

}
