package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Doctor;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Generator.*;

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
        Doctor ep = new Doctor("");
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), ep.getMedical_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), ep.getAcademic_title_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "gender"), String.valueOf(ep.getGender()));
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), ep.getName());
        Assert.assertEquals(Helper.s_ParseJson(data, "birthday"), ep.getBirthday().replace('/', '-'));
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), ep.getMajor_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), ep.getHospital_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), hospitalName(ep.getHospital_id()));
        Assert.assertEquals(Helper.s_ParseJson(data, "honour"), ep.getHonour());
//        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), KBHospital_Detail.s_Detail(ep.getHospital_id()).get("city_id"));
//        Assert.assertEquals(Helper.s_ParseJson(data, "city_name"), cityName(KBHospital_Detail.s_Detail(ep.getHospital_id()).get("city_id")));
    }

    @Test
    public void test_02_获取医库医生详情_无效ID() {
        String res = "";
        String expertId = KBExpert_Create.s_Create(new Doctor(""));
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", "111"+expertId);
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
