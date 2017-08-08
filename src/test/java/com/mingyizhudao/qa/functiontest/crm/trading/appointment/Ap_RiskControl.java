package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.Appointment;
import net.sf.json.JSONObject;
import org.testng.Assert;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import static com.mingyizhudao.qa.utilities.Helper.*;

public class Ap_RiskControl extends BaseTest{
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/surgeons";

    public static boolean s_Take(String orderNumber) {
        String[] doctor_list = new String[] {randomExpertId(), randomExpertId()};
        return s_Take(orderNumber, doctor_list);
    }

    public static boolean s_Take(String orderNumber, String[] doctor_list) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", true);
        body.put("content", "脚本测试——风控通过");
        body.put("risk_control_helper", "方超男");
        String expert_id = doctor_list[0];
        body.put("major_recommended_doctor_id", expert_id);
        body.put("major_recommended_doctor_name", expertName(expert_id));
        if (doctor_list.length == 2) {
            expert_id = doctor_list[1];
            body.put("minor_recommended_doctor_id", expert_id);
            body.put("minor_recommended_doctor_name", expertName(expert_id));
        }
        String res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("2000") ? true : false;
    }

    public static boolean s_Reject(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        body.put("content", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超男");
        String res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("9000") ? true : false;
    }

    public void test_01_风控推荐一名医生() {

    }

    public void test_02_风控推荐二名医生() {

    }

    public void test_03_风控不通过() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        body.put("content", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超男");
        res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

    public void test_04_风控推荐医生信息不完整() {

    }

    public void test_05_风控不通过理由不完整() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
//        body.put("content", "脚本测试——风控不通过");
//        body.put("risk_control_helper", "方超男");
        res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

    public void test_06_风控result字段为空() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", "hi test");
        body.put("content", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超男");
        res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.remove("result");
        res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    public void test_07_风控result字段false时有成功信息() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        body.put("content", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超男");
        String expert_id = randomExpertId();
        body.put("major_recommended_doctor_id", expert_id);
        body.put("major_recommended_doctor_name", expertName(expert_id));
        res = s_SendPut(host_appointment + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_id"));
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_name"));
    }

}
