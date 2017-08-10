package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Doctor;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.HttpRequest.*;

/**
 * Created by dayi on 2017/7/20.
 */
public class KBExpert_Diseases_V2 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v2/medicallibrary/doctors/{id}/SaveDoctorRelativeDisease";

    public static boolean s_Connect(String expertId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        JSONObject body = DiseaseJson(5, 4);
        res = s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        if(!JSONObject.fromObject(res).getString("code").equals("1000000")) return false;
        return true;
    }

    @Test
    public void test_01_关联专业和疾病_已同步的医生() {
        String res = "";
        HashMap<String, String> info = s_CreateSyncedDoctor(new User());
        if (info == null) {
            Assert.fail("创建医库医生失败，退出用例执行");
        }
        String expertId = info.get("expert_id");
        String doctorId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);
        JSONObject body = DiseaseJson(5, 4);
        res = s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "specialty_list()"), "5");

        res = RegisteredDoctor_Detail.s_Detail(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "exp_list()"), "5");
    }

    @Test
    public void test_02_已关联专业下增加和减少疾病() {

    }

    @Test
    public void test_03_增加和减少关联专业() {

    }

    @Test
    public void test_04_关联专业ID有误() {

    }

    @Test
    public void test_05_关联专业下的疾病ID有误() {

    }

    @Test
    public void test_06_关联专业和疾病_未注册医生() {
        String res = "";
        Doctor ep = new Doctor();
        String expertId = KBExpert_Create.s_Create(ep);
        if (expertId == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", expertId);

        JSONObject body = DiseaseJson(5, 4);
        res = s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "specialty_list()"), "5");
    }

    public static JSONObject DiseaseJson(int cat_count, int dis_count) {
        JSONObject body = new JSONObject();
        JSONArray specialty_list = new JSONArray();
        JSONObject specialty = new JSONObject();
        for(int i=0; i<cat_count; i++) {
            String categoryId = Generator.randomMajorId();
            specialty.put("category", JSONObject.fromObject("{\"id\": " + categoryId + ",\"name\": \"" + Generator.majorName(categoryId) + "\"}"));
            for(int j=0; j<dis_count; j++) {
                String diseaseId = Generator.randomDiseaseIdUnder(categoryId);
                specialty.accumulate("disease_list", JSONObject.fromObject("{\"id\": "+diseaseId+",\"name\": \""+ Generator.diseaseName(diseaseId)+"\"}"));
            }
            specialty_list.add(specialty);
            specialty.clear();
        }
        body.put("specialty_list", specialty_list);
        return body;
    }
}
