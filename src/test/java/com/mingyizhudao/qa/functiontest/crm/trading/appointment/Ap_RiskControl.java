package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

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
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", true);
        body.put("content", "脚本测试——风控通过");
        body.put("risk_control_helper", "方超（男）");
        String expert_id = doctor_list[0];
        body.put("major_recommended_doctor_id", expert_id);
        if (doctor_list.length == 2) {
            expert_id = doctor_list[1];
            body.put("minor_recommended_doctor_id", expert_id);
        }
        String res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        String status = r.getJSONObject("data").getString("status");
        return status.equals("2000");
    }

    public static boolean s_Reject(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        body.put("content", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超（男）");
        String res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("9000");
    }

    @Test
    public void test_01_风控推荐一名医生() {
        String res = "";
        String orderNumber = s_Create(new AppointmentOrder());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", true);
        body.put("content", "脚本测试——风控通过");
        body.put("risk_control_helper", "方超（男）");
        String expert_id = randomExpertId();
        body.put("major_recommended_doctor_id", expert_id);
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "major_recommended_doctor_id"), body.getString("major_recommended_doctor_id"));
        Assert.assertNotNull(s_ParseJson(data, "major_recommended_doctor_referrer_name"));

    }

    @Test
    public void test_02_风控推荐二名医生() {
        String res = "";
        String orderNumber = s_Create(new AppointmentOrder());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", true);
        body.put("content", "脚本测试——风控通过");
        body.put("risk_control_helper", "方超（男）");
        String expert_id = randomExpertId();
        body.put("major_recommended_doctor_id", expert_id);
        expert_id = randomExpertId();
        body.put("minor_recommended_doctor_id", expert_id);
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "major_recommended_doctor_id"), body.getString("major_recommended_doctor_id"));
        Assert.assertNotNull(s_ParseJson(data, "minor_recommended_doctor_referrer_name"));
        Assert.assertEquals(s_ParseJson(data, "minor_recommended_doctor_id"), body.getString("minor_recommended_doctor_id"));
        Assert.assertNotNull(s_ParseJson(data, "major_recommended_doctor_referrer_name"));
    }

    @Test
    public void test_03_风控不通过() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        body.put("reason", "脚本测试——风控不通过原因");
        body.put("content", "脚本测试——风控不通过备注");
        body.put("risk_control_helper", "方超男");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "status"), "9000");
    }

    //TODO
    public void test_04_风控推荐医生信息有误() {

    }

    @Test
    public void test_05_风控不通过理由不完整() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_06_风控result字段不正确() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", "hi test");
        body.put("content", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超男");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        body.remove("result");
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_07_风控result字段false时有成功信息() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("result", false);
        body.put("reason", "脚本测试——风控不通过");
        body.put("risk_control_helper", "方超（男）");
        String expert_id = randomExpertId();
        body.put("major_recommended_doctor_id", expert_id);
        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_id"));
        Assert.assertNull(s_ParseJson(data, "major_recommended_doctor_name"));
    }

}
