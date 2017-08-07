package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 16/5/2017.
 */
public class KBExpert_Detail extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/medicallibrary/doctors/{id}";

    public static String s_Detail(String expertId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        if (expertId == null) return null;
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",expertId);
        res = HttpRequest.s_SendGet(host_crm+uri,"", crm_token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取医库医生详情_有效ID() {

        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",info.get("id"));
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), ep.body.getString("academic_title_list"));
        Assert.assertEquals(Helper.s_ParseJson(data, "gender"), ep.body.getString("gender"));
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), ep.body.getString("name"));
        Assert.assertEquals(Helper.s_ParseJson(data, "birthday"), ep.body.getString("birthday").replace('/', '-'));
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), ep.body.getString("major_id"));
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), KnowledgeBase.kb_hospital.get(ep.body.getString("hospital_id")));
        Assert.assertEquals(Helper.s_ParseJson(data, "honour"), ep.body.getString("honour"));
        String expert_city_id = Helper.s_ParseJson(data, "city_id");
        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(ep.body.getString("hospital_id"));

        String hospital_city_id = hospitalInfo.get("city_id");
        Assert.assertEquals(hospital_city_id, expert_city_id);

        Assert.assertEquals(Helper.s_ParseJson(data, "certified_status"), "NOT_CERTIFIED");
        Assert.assertEquals(Helper.s_ParseJson(data, "signed_status"), "NOT_SIGNED");
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
    }

    @Test
    public void test_02_获取医库医生详情_无效ID() {

        String res = "";
        HashMap<String, String> info = KBExpert_Create.s_Create(new ExpertProfile(true));
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", "111"+info.get("id"));
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
