package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;

/**
 * Created by ttshmily on 2/6/2017.
 */
public class RegisteredDoctor_Sign extends BaseTest {

/*    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/expert_verifications";

    @Test(enabled = false)
    public void test_01_通过专家认证_已认证的医生() {

        String res = "";
        DoctorProfile_Test dp = new DoctorProfile_Test(true);
        HashMap<String, String> info = s_CreateVerifiedDoctor(dp);
        String doctorId = info.get("id");
        String expertId = info.get("expert_id");
        if (doctorId == null) Assert.fail("创建医生失败，认证用例无法执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        JSONObject body = new JSONObject();
        body.put("status", "SIGNED");
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "SIGNED");



        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "SIGNED");
    }

    @Test(enabled = false)
    public void test_02_通过专家认证_未认证的医生() {
        String res = "";
        DoctorProfile_Test dp = new DoctorProfile_Test(true);
        HashMap<String, String> info = s_CreateRegisteredDoctor(dp);
        String doctorId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        JSONObject body = new JSONObject();
        body.put("status", "SIGNED");
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "NOT_SIGNED");
    }*/
}
