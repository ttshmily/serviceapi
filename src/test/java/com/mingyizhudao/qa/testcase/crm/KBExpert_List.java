package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ttshmily on 16/5/2017.
 */
public class KBExpert_List extends BaseTest {
    public static final Logger logger= Logger.getLogger(KBExpert_List.class);
    public static String uri = "/api/v1/medicallibrary/doctors";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static int expertList() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        return Integer.parseInt(parseJson(JSONObject.fromObject(res), "data:size"));

    }

    @Test
    public void test_01_获取医库医生列表_使用默认值() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm + uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(parseJson(data, "list()"), "医生列表为空");
        Assert.assertNotEquals(parseJson(data, "list(0):id"), "", "医生ID为空");
        Assert.assertNotEquals(parseJson(data, "list(0):hospital_name"), "", "注册时间没有值");
        Assert.assertNotEquals(parseJson(data, "list(0):name"), "", "医生姓名为空");
        Assert.assertNotEquals(parseJson(data, "list(0):mobile"), "", "医生手机为空");
        Assert.assertNotEquals(parseJson(data, "list(0):hospital_name"), "", "医生医院名称为空");
        Assert.assertNotEquals(parseJson(data, "list(0):hospital_id"), "", "医生医院ID为空");
        Assert.assertNotNull(parseJson(data, "list(0):major"), "医生专业字段不存在");
//        Assert.assertNotNull(parseJson(data, "list(0):academic_title"), "医生学术职称不存在");
//        Assert.assertNotNull(parseJson(data, "list(0):medical_title"), "医生技术职称不存在");
//        Assert.assertNotEquals(parseJson(data, "list(0):is_verified"), "", "医生是否认证字段没有值");
//        Assert.assertNotNull(parseJson(data, "list(0):inviter_name"), "医生的地推字段不存在");
//        Assert.assertNotNull(parseJson(data, "list(0):is_famous"), "医生是否是专家字段不存在");
        Assert.assertNotNull(parseJson(data, "size"), "列表总量字段不存在");
        Assert.assertEquals(parseJson(data, "page_size"), "10", "默认分页大小不为10");
        Assert.assertNotNull(parseJson(data, "page"), "默认没有传回第1页");
        Assert.assertEquals(parseJson(data, "list()"), "10", "分页的默认值不为10");
        Integer id1 = Integer.parseInt(parseJson(data, "list(0):id"));
        Integer id2 = Integer.parseInt(parseJson(data, "list(4):id"));
        Integer id3 = Integer.parseInt(parseJson(data, "list(9):id"));

        query.put("page","2");
        try {
            res = HttpRequest.sendGet(host_crm + uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Integer id4 = Integer.parseInt(parseJson(data, "list(0):id"));

        if (!(id1 > id2 && id2 > id3 && id3 > id4)) Assert.fail("没有按照医生ID倒序排列");
    }

    @Test
    public void test_02_获取医库医生列表_传入特定的页码和分页大小() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "");
        query.put("pageSize", "10");
        int total = expertList();
        int pageSize = 10;
        int pageNum = total / pageSize + 1;
        // 默认分页大小10
        for (int i = 1; i < pageNum; i++) {
            query.replace("page",String.valueOf(i));
            try {
                res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
                Assert.fail("请求出错");
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "list()"), "10", "分页的默认值不为10");
            Assert.assertEquals(parseJson(data, "size"), String.valueOf(total), "列表总数量错误");
            Assert.assertEquals(parseJson(data, "page"), String.valueOf(i), "页码错误");
        }

        // 设置分页大小为随机正确值，执行10次
        for (int i = 0; i < 10; i++) {
            query.replace("page", "1");
            int page_size = (int) UT.randomInt(200);
            query.replace("pageSize", String.valueOf(page_size));
            try {
                res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
                checkResponse(res);
                Assert.assertEquals(code, "1000000");
                Assert.assertEquals(parseJson(data, "list()"), String.valueOf(page_size), "分页值不正确");
            } catch (IOException e) {
                logger.error(e);
                Assert.fail("请求出错");
            }

            // 设置页数超出总量时，返回列表为空
            int page = total/page_size + 1;
            query.replace("page", String.valueOf(page+1));
            try {
                res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
                checkResponse(res);
                Assert.assertEquals(code, "1000000");
                Assert.assertEquals(parseJson(data, "list()"), "0", "页码超出总数时，应当返回空列表");
            } catch (IOException e) {
                logger.error(e);
            }

        }

    }

    @Test
    public void test_03_获取医库医生列表_传入特定的医生姓名() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        //以姓名进行搜索
        query.put("DoctorName","钟西北");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray exp_list = data.getJSONArray("list");
        for (int i=0; i<exp_list.size(); i++) {
            JSONObject exp = exp_list.getJSONObject(i);
            Pattern p = Pattern.compile("钟西北");
            Matcher m = p.matcher(exp.getString("name"));
            Assert.assertTrue(m.find(), "姓名搜索结果不准确");
        }
    }

    @Test
    public void test_04_获取医库医生列表_传入特定的医生手机() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String mobile = UT.randomPhone();
        query.put("mobile", mobile);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("mobile"), mobile);
        }

        query.replace("mobile",mainMobile);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("mobile"), mainMobile);
        }

    }

    @Test
    public void test_05_获取医库医生列表_传入特定的技术职称() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String medical = UT.randomMedicalId();
        query.put("medical_title_list", medical);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("medical_title_list"), medical);
        }

        medical = UT.randomMedicalId().concat(",").concat(UT.randomMedicalId());
        query.replace("medical_title_list",medical);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            String title = doc.getString("medical_title_list");
            Assert.assertTrue(medical.contains(title));
        }

        query.replace("medical_title_list","-1");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
    }

    @Test
    public void test_06_获取医生列表_传入特定的学术职称() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String academic = UT.randomAcademicId();
        query.put("academic_title_list",academic);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("academic_title_list"), academic);
        }

        academic = UT.randomAcademicId().concat(",").concat(UT.randomAcademicId());
        query.replace("academic_title_list",academic);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            String title = doc.getString("academic_title_list");
            Assert.assertTrue(academic.contains(title));
        }

        query.replace("academic_title_list","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_07_获取医库医生列表_传入特定的医院ID() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String hospital_id = UT.randomHospitalId();
        query.put("hospital_id", hospital_id);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("hospital_id"), hospital_id);
        }

        query.replace("hospital_id",mainDoctorHospitalId);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("hospital_id"), mainDoctorHospitalId);
        }

    }

    @Test
    public void test_07_获取医库医生列表_获取认证专家() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        query.put("signed_status","1");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray exp_list = data.getJSONArray("list");
        for (int i=0; i<exp_list.size(); i++) {
            JSONObject exp = exp_list.getJSONObject(i);
            Assert.assertEquals(exp.getString("signed_status"), "SIGNED");
        }
    }

    @Test
    public void test_08_获取医库医生列表_根据专业搜索() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String major_id = UT.randomMajorId();
        query.put("major_id", major_id);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray exp_list = data.getJSONArray("list");
        for (int i=0; i<exp_list.size(); i++) {
            JSONObject exp = exp_list.getJSONObject(i);
            Assert.assertEquals(exp.getString("major_id"), major_id);
        }
    }

    @Test
    public void test_09_获取医库医生列表_根据疾病搜索() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String disease_id = UT.randomDiseaseId();
        query.put("disease_id", disease_id);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray exp_list = data.getJSONArray("list");
        for (int i=0; i<exp_list.size(); i++) {
            JSONObject exp = exp_list.getJSONObject(i);
            JSONArray diseaseList = exp.getJSONArray("disease_list");
            List<String> list = new ArrayList<>();
            for (int j=0; j<diseaseList.size(); j++) {
                JSONObject dis = diseaseList.getJSONObject(j);
                list.add(dis.getString("disease_id"));
            }
            Assert.assertTrue(list.contains(disease_id));
        }
    }

    @Test
    public void test_10_获取医库医生列表_根据认证状态() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        query.put("is_registered","1");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray exp_list = data.getJSONArray("list");
        for (int i=0; i<exp_list.size(); i++) {
            JSONObject exp = exp_list.getJSONObject(i);
            Assert.assertEquals(exp.getString("certified_status"), "CERTIFIED");
        }
    }
}
