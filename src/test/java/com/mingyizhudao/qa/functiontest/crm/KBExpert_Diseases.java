package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ttshmily on 31/5/2017.
 */
public class KBExpert_Diseases extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/medicallibrary/doctors/{doctor_id}/SaveDoctorRelativeDisease";

    public static boolean s_Connect(String expertId, List<String> disease_ids) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);
        JSONObject diseaseList = new JSONObject();
        for(int i=0; i<disease_ids.size(); i++) {
            String diseaseId = disease_ids.get(i);
        }
        diseaseList.put("disease_ids", disease_ids.toArray());
        logger.debug(diseaseList.toString());
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        JSONObject node = JSONObject.fromObject(res);
        if(!node.getString("code").equals("1000000")) return false;
        return true;
    }

    @Test
    public void test_01_关联疾病() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);

        List<String> inputString = new ArrayList<>();
        JSONObject diseaseList = new JSONObject();
        String id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        System.out.println(inputString);

        List<String> outputString = new ArrayList<>();
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "关联疾病失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        JSONArray disease_list = data.getJSONArray("disease_list");
        for (int i=0; i<disease_list.size(); i++) {
            JSONObject disease = disease_list.getJSONObject(i);
            outputString.add(disease.getString("disease_id"));
        }
        System.out.println(outputString);
        Assert.assertEquals(outputString.size(), inputString.size(), "关联疾病的数量不相同");
        for (int i=0; i<outputString.size(); i++) {
            Assert.assertTrue(inputString.contains(outputString.get(i)));
        }
        for (int i=0; i<inputString.size(); i++) {
            Assert.assertTrue(outputString.contains(inputString.get(i)));
        }
    }

    @Test
    public void test_02_关联疾病_增加() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);

        List<String> inputString = new ArrayList<>();
        JSONObject diseaseList = new JSONObject();
        String id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        System.out.println(inputString);

        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "关联疾病失败");

//增加3个疾病
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "关联疾病失败");


        List<String> outputString = new ArrayList<>();
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        JSONArray disease_list = data.getJSONArray("disease_list");
        for (int i=0; i<disease_list.size(); i++) {
            JSONObject disease = disease_list.getJSONObject(i);
            outputString.add(disease.getString("disease_id"));
        }
        System.out.println(outputString);
        Assert.assertEquals(outputString.size(), inputString.size(), "关联疾病的数量不相同");
        for (int i=0; i<outputString.size(); i++) {
            Assert.assertTrue(inputString.contains(outputString.get(i)));
        }
        for (int i=0; i<inputString.size(); i++) {
            Assert.assertTrue(outputString.contains(inputString.get(i)));
        }
    }

    @Test
    public void test_03_关联疾病_减少() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);

        List<String> inputString = new ArrayList<>();
        JSONObject diseaseList = new JSONObject();
        String id1 = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id1);
        inputString.add(id1);
        String id2 = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id2);
        inputString.add(id2);
        String id3 = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id3);
        inputString.add(id3);
        String id4 = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id4);
        inputString.add(id4);
        String id5 = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id5);
        inputString.add(id5);
        System.out.println(inputString);

        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "关联疾病失败");

//减少3个疾病
        diseaseList.clear();
        diseaseList.accumulate("disease_ids", id1);
        diseaseList.accumulate("disease_ids", id3);
        diseaseList.accumulate("disease_ids", id5);
        inputString.clear();
        inputString.add(id1);
        inputString.add(id3);
        inputString.add(id5);
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "关联疾病失败");


        List<String> outputString = new ArrayList<>();
        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        JSONArray disease_list = data.getJSONArray("disease_list");
        for (int i=0; i<disease_list.size(); i++) {
            JSONObject disease = disease_list.getJSONObject(i);
            outputString.add(disease.getString("disease_id"));
        }
        System.out.println(outputString);
        Assert.assertEquals(outputString.size(), inputString.size(), "关联疾病的数量不相同");
        for (int i=0; i<outputString.size(); i++) {
            Assert.assertTrue(inputString.contains(outputString.get(i)));
        }
        for (int i=0; i<inputString.size(); i++) {
            Assert.assertTrue(outputString.contains(inputString.get(i)));
        }
    }

    @Test
    public void test_04_去重疾病() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);

        List<String> inputString = new ArrayList<>();
        JSONObject diseaseList = new JSONObject();
        String id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        //以下为重复内容
        diseaseList.accumulate("disease_ids", id);
        diseaseList.accumulate("disease_ids", id);
        diseaseList.accumulate("disease_ids", id);
        diseaseList.accumulate("disease_ids", id);
        System.out.println(inputString);

        List<String> outputString = new ArrayList<>();
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "关联疾病失败");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        JSONArray disease_list = data.getJSONArray("disease_list");
        for (int i=0; i<disease_list.size(); i++) {
            JSONObject disease = disease_list.getJSONObject(i);
            outputString.add(disease.getString("disease_id"));
        }
        System.out.println(outputString);
        Assert.assertEquals(outputString.size(), inputString.size(), "关联疾病的数量不相同");
        for (int i=0; i<outputString.size(); i++) {
            Assert.assertTrue(inputString.contains(outputString.get(i)));
        }
        for (int i=0; i<inputString.size(); i++) {
            Assert.assertTrue(outputString.contains(inputString.get(i)));
        }
    }

    @Test
    public void test_05_去除错误疾病ID() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);

        List<String> inputString = new ArrayList<>();
        JSONObject diseaseList = new JSONObject();
        String id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        //以下为错误ID内容
        diseaseList.accumulate("disease_ids", "11"+id);
        diseaseList.accumulate("disease_ids", "22"+id);
        diseaseList.accumulate("disease_ids", "33"+id);
        diseaseList.accumulate("disease_ids", "44"+id);
        System.out.println(inputString);

        List<String> outputString = new ArrayList<>();
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

//        res = KBExpert_Detail.s_Detail(expertId);
//        s_CheckResponse(res);
//        JSONArray disease_list = data.getJSONArray("disease_list");
//        for (int i=0; i<disease_list.size(); i++) {
//            JSONObject disease = disease_list.getJSONObject(i);
//            outputString.add(disease.getString("disease_id"));
//        }
//        System.out.println(outputString);
//        Assert.assertEquals(outputString.size(), inputString.size(), "关联疾病的数量不相同");
//        for (int i=0; i<outputString.size(); i++) {
//            Assert.assertTrue(inputString.contains(outputString.get(i)));
//        }
//        for (int i=0; i<inputString.size(); i++) {
//            Assert.assertTrue(outputString.contains(inputString.get(i)));
//        }
    }

    @Test
    public void test_06_去除所有疾病ID() {
        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.s_Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        String expertId = info.get("id");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("doctor_id", expertId);

        List<String> inputString = new ArrayList<>();
        JSONObject diseaseList = new JSONObject();
        String id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        id = Generator.randomDiseaseId();
        diseaseList.accumulate("disease_ids", id);
        inputString.add(id);
        System.out.println(inputString);

        List<String> outputString = new ArrayList<>();
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        diseaseList.replace("disease_ids","[]");
        try {
            res = HttpRequest.s_SendPost(host_crm+uri, diseaseList.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = KBExpert_Detail.s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(data.getJSONArray("disease_list").size(), 0);
    }
}
