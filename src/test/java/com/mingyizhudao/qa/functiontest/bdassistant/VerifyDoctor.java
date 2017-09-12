package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Generator.randomEmployeeId;

/**
 * Created by dayi on 2017/7/17.
 */
public class VerifyDoctor extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/verified";

    @Test
    public void test_01_地推认证医生_认证为专家() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        String doctorId = s_CreateRegisteredDoctor(new User()).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);

        pathValue.put("id", doctorId);
        body.put("approve_status", "1");  // 认证成功
        body.put("signed_status", "SIGNED");  // 认证成功
        body.put("staff_id", randomEmployeeId());  // 认证成功
        res = HttpRequest.s_SendPut(host_bda + uri, body.toString(), bda_session_staff, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        String is_verified = Helper.s_ParseJson(data, "is_verified");
        String is_signed = Helper.s_ParseJson(data, "signed_status");
        Assert.assertEquals(is_verified, "1");
        Assert.assertNotNull(Helper.s_ParseJson(data, "date_verified"));
        Assert.assertEquals(is_signed, "SIGNED");
        Assert.assertNotNull(Helper.s_ParseJson(data, "signed_time"));
    }

    @Test
    public void test_02_地推认证医生_不认证为专家() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        JSONObject body = new JSONObject();

        String doctorId = s_CreateRegisteredDoctor(new User()).get("id");
        if (doctorId == null)
            Assert.fail("创建医生失败，认证用例无法执行");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);

        pathValue.put("id", doctorId);
        body.put("approve_status", "1");  // 认证成功
        body.put("staff_id", randomEmployeeId());  // 认证成功
        res = HttpRequest.s_SendPut(host_bda + uri, body.toString(), bda_session_staff, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        String is_verified = Helper.s_ParseJson(data, "is_verified");
        String is_signed = Helper.s_ParseJson(data, "signed_status");
        Assert.assertEquals(is_verified, "1");
        Assert.assertNotNull(Helper.s_ParseJson(data, "date_verified"));
        Assert.assertEquals(is_signed, "NOT_SIGNED");
    }

}
