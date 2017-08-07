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
import java.util.HashMap;
import java.util.List;

/**
 * Created by dayi on 2017/6/23.
 */
public class DoctorListV2 extends BaseTest {
    public static final Logger logger= Logger.getLogger(DoctorListV2.class);
    public static String uri = "/api/v2/doctors/doctorList";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    @Test
    public void test_01_没有token或token错误无权限使用接口() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");

        res = HttpRequest.s_SendGet(host_bda + uri, query, "aaa");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "token错误不应该调用成功");
    }

    @Test
    public void test_02_登录用户_主管查看下属() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        query.put("agent_contact_id", agent_contact_id);

        //TODO 每个地推所对应的城市需要提前准备好
        String cityId = "";

        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "主管查看下属应该成功");

        JSONArray doctor_list = data.getJSONArray("list");
        for(int i=0; i<doctor_list.size(); i++) {
            JSONObject doctor = doctor_list.getJSONObject(i);
            // TODO 判断条件需要重写
            Assert.assertEquals(doctor.getString("city_id"), cityId);
        }
    }

    @Test
    public void test_03_登录主管用户_不能查看非下属的数据() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = "SH0001";
        query.put("agent_contact_id", agent_contact_id);

        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "主管查看非下属不应该成功");
    }

    @Test
    public void test_04_查看医生列表_不传入城市ID得到所有负责区域的医生() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        query.put("agent_contact_id", agent_contact_id);


        // TODO
        String cityId = "";

        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");
        // TODO
        JSONArray doctor_list = data.getJSONArray("list");
        for(int i=0; i<doctor_list.size(); i++) {
            JSONObject doctor = doctor_list.getJSONObject(i);
            // TODO 判断条件需要重写
            Assert.assertEquals(doctor.getString("city_id"), cityId);
        }
    }

    @Test
    public void test_05_查看医生列表_传入城市ID得到该城市区域的医生() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        query.put("agent_contact_id", agent_contact_id);

        HashMap<String, List<String>> cities = PersonalInfoV2.BDInfo(bda_token_staff);
        // TODO
        String cityId = "";
        query.put("city_id", cityId);
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");

        JSONArray doctor_list = data.getJSONArray("list");
        for(int i=0; i<doctor_list.size(); i++) {
            JSONObject doctor = doctor_list.getJSONObject(i);
            // TODO 判断条件需要重写
            Assert.assertEquals(doctor.getString("city_id"), cityId);
        }
    }

    @Test
    public void test_06_查看医生列表_传入medical_title() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        query.put("agent_contact_id", agent_contact_id);

        // TODO
        String cityId = "";
        query.put("city_id", cityId);

        String medical_title_list = Generator.randomMedicalId();
        query.put("medical_title", medical_title_list);
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");

        JSONArray doctor_list = data.getJSONArray("list");
        for(int i=0; i<doctor_list.size(); i++) {
            JSONObject doctor = doctor_list.getJSONObject(i);
            // TODO 判断条件需要重写
            Assert.assertEquals(doctor.getString("city_id"), cityId);
            Assert.assertEquals(doctor.getString("medical_title_list"), medical_title_list);
        }
    }

    @Test
    public void test_06_查看医生列表_传入academic_title() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = Generator.randomEmployeeId();
        query.put("agent_contact_id", agent_contact_id);

        // TODO
        String cityId = "";
        query.put("city_id", cityId);

        String academic_title_list = Generator.randomAcademicId();
        query.put("academic_title", academic_title_list);
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");

        JSONArray doctor_list = data.getJSONArray("list");
        for(int i=0; i<doctor_list.size(); i++) {
            JSONObject doctor = doctor_list.getJSONObject(i);
            // TODO 判断条件需要重写
            Assert.assertEquals(doctor.getString("city_id"), cityId);
            Assert.assertEquals(doctor.getString("academic_title_list"), academic_title_list);
        }
    }

    @Test
    public void test_06_查看医生列表_自己查看自己() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String agent_contact_id = "SH9999";
        query.put("agent_contact_id", agent_contact_id);

        // TODO
        String cityId = "";
        query.put("city_id", cityId);

        String academic_title_list = Generator.randomAcademicId();
        query.put("academic_title", academic_title_list);
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_token_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");

        JSONArray doctor_list = data.getJSONArray("list");
        for(int i=0; i<doctor_list.size(); i++) {
            JSONObject doctor = doctor_list.getJSONObject(i);
            // TODO 判断条件需要重写
            Assert.assertEquals(doctor.getString("city_id"), cityId);
            Assert.assertEquals(doctor.getString("academic_title_list"), academic_title_list);
        }
    }
}
