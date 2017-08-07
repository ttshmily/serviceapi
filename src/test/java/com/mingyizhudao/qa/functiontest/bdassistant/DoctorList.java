package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.utilities.Helper;
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
 * Created by ttshmily on 17/5/2017.
 */
@Test(enabled = false)
public class DoctorList extends BaseTest {

    public static final Logger logger= Logger.getLogger(DoctorList.class);
    public static String uri = "/api/v1/doctor/doctorList";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    @Test
    public void test_01_未登录用户无权限使用接口() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, map, "", null);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");
    }

    @Test
    public void test_02_登录用户_不传入地推ID获取个人医生列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", Generator.randomEmployeeId());
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):name"), "医生的name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):hospital_name"), "医生的hospital_name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):academic_title"), "医生的academic_title字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):medical_title"), "医生的academic_title字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):doctor_card_pictures"), "医生的academic_title字段缺失");
    }

    @Test
    public void test_03_登录用户_传入自己的地推ID获取个人医生列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0133");
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):name"), "医生的name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):hospital_name"), "医生的hospital_name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):academic_title"), "医生的academic_title字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):medical_title"), "医生的academic_title字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):doctor_card_pictures"), "医生的academic_title字段缺失");
    }

    @Test
    public void test_04_登录用户_传入下属地推ID获取其医生列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        map.put("agent_contact_id", agent_contact_id);
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):name"), "医生的name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):hospital_name"), "医生的hospital_name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):academic_title"), "医生的academic_title字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):medical_title"), "医生的academic_title字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):doctor_card_pictures"), "医生的academic_title字段缺失");
    }

    @Test
    public void test_05_登录用户_传入非下属地推ID无法获取其医生列表() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0001");
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "非下属的ID不应该调用成功");
    }

    @Test
    public void test_06_医生列表_返回数据有认证时间字段和第一笔成单时间() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        map.put("agent_contact_id", agent_contact_id);
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):create_order_time"), "没有第一次创建订单的时间");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):date_verified"), "没有认证通过的时间");

    }

    @Test
    public void test_07_输入检测_分页逻辑() {

    }

    public void test_08_输入检测_过滤是否激活() {

    }

    public void test_09_输入检测_过滤医院ID() {

    }

    public void test_10_输入检测_过滤学术职称() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0105");
        String title = Generator.randomKey(KnowledgeBase.kb_academic_title);
        map.put("academic_title", title);
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        JSONArray doctorList = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
        for (int i=0; i<doctorList.size(); i++) {
            JSONObject doc = doctorList.getJSONObject(i);
            Assert.assertEquals(Helper.s_ParseJson(doc, "academic_title_list"), title, "academic_title过滤后有其他的titil存在");
        }

    }

    public void test_11_输入检测_过滤技术职称() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0105");
        String title = Generator.randomKey(KnowledgeBase.kb_medical_title);
        map.put("medical_title", title);
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        JSONArray doctorList = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
        for (int i=0; i<doctorList.size(); i++) {
            JSONObject doc = doctorList.getJSONObject(i);
            Assert.assertEquals(Helper.s_ParseJson(doc, "medical_title_list"), title, "medical_title过滤后有其他的titil存在");
        }

    }

    public void test_12_输入检测_过滤医生姓名() {

    }

    public void test_13_输入检测_过滤医生手机() {

    }

    public void test_14_输入检测_过滤认证状态() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        map.put("agent_contact_id", "SH0105");
        map.put("is_verified", "1");
        List<String> status = new ArrayList<>();
        status.add("-1");
        status.add("0");
        status.add("1");
        status.add("2");
        for (String s:status
             ) {
            map.replace("is_verified", s);
            res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token, null);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000", "有token应该调用成功");
            JSONArray doctorList = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < doctorList.size(); i++) {
                JSONObject doc = doctorList.getJSONObject(i);
                Assert.assertEquals(Helper.s_ParseJson(doc, "is_verified"), s, "过滤后有杂质状态存在");
            }
        }
    }
}
