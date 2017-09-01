package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_Detail extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static final String uri = version+"/doctors/{id}/profiles";

    public static String s_Detail(String regId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",regId);
        res = HttpRequest.s_SendGet(host_crm+uri,"", crm_token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取医生详情_有效ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "medical_title_list"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "academic_title_list"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "inviter_name"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "hospital_name"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "icon"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "audit_state"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "staff_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "staff_name"));
    }

    @Test
    public void test_02_获取医生详情_无效ID() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id","1"+mainDoctorId);
        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}