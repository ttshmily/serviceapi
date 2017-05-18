package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_Modify extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_Modify.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/profiles";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_CRM更新医生详情_综合正确调用() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);

        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生信息");
        body.put("department","科室综合");
        String city = UT.randomKey(KB.kb_city);
        String hospital = UT.randomKey(KB.kb_hospital);
        String major = UT.randomKey(KB.kb_major);
        String academic = UT.randomKey(KB.kb_academic_title);
        String medical = UT.randomKey(KB.kb_medical_title);
        body.put("city_id",city);
        body.put("hospital_id",hospital);
        body.put("major_id", major);
        body.put("academic_title", academic);
        body.put("medical_title", medical);
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
//        Assert.assertEquals(parseJson(data, "department"), "科室综合");
        Assert.assertEquals(parseJson(data, "hospital_id"), hospital);
        Assert.assertEquals(parseJson(data, "city_id"), city);
        Assert.assertEquals(parseJson(data, "major_id"), major);
        Assert.assertEquals(parseJson(data, "academic_title_list"), academic);
        Assert.assertEquals(parseJson(data, "medical_title_list"), medical);

        // 错误的医生ID，应该更新失败
        pathValue.replace("id", mainDoctorId+"11111");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test(enabled = false)
    public void test_02_CRM更新医生详情_更新姓名() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生姓名");
        // 更新正确的name，应当成功
        body.put("name", "美女医生");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
        } catch (IOException e) {
            logger.error(e);
        }
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "name"), "美女医生");
    }

    @Test
    public void test_03_CRM更新医生详情_更新所在医院() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生医院信息");

        // 更新hospital_id，应当成功
        String hospitalId = UT.randomKey(KB.kb_hospital);
        body.put("hospital_id", hospitalId);
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
        } catch (IOException e) {
            logger.error(e);
        }
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(parseJson(data, "hospital_name"), KB.kb_hospital.get(hospitalId));

        // 更新hospital_id和hospital_name，应当以hospital_id为准。
        hospitalId = UT.randomKey(KB.kb_hospital);
        body.replace("hospital_id", hospitalId);
        body.put("hospital_name", "测试医院");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
        } catch (IOException e) {
            logger.error(e);
        }
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(parseJson(data, "hospital_name"), KB.kb_hospital.get(hospitalId));
    }

    @Test
    public void test_04_CRM更新医生详情_更新学术职称() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生学术职称信息");

        // 更新正确的academic_title，应当成功
        String academic = UT.randomKey(KB.kb_academic_title);
        body.put("academic_title", academic);
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "academic_title_list"), academic);
        Assert.assertEquals(parseJson(data, "academic_title"), KB.kb_academic_title.get(academic));

        // 更新错误的academic_title，应当不成功
        body.replace("academic_title", "ASSOCIATE_PROFESSOR_WRONG");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "academic_title_list"), academic);

    }

    @Test
    public void test_05_CRM更新医生详情_更新技术职称() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生技术职称信息");

        // 更新正确的medical_title，应当成功
        String medical = UT.randomKey(KB.kb_medical_title);
        body.put("medical_title", medical);
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "medical_title_list"), medical);
        Assert.assertEquals(parseJson(data, "medical_title"), KB.kb_medical_title.get(medical));

        // 更新错误的medical_title，应当不成功
        body.replace("medical_title", "ARCHIATER_WRONG");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "medical_title_list"), medical);
        Assert.assertEquals(parseJson(data, "medical_title"), KB.kb_medical_title.get(medical));
    }

    @Test
    public void test_06_CRM更新医生详情_更新医生专业() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生专业信息");

        // 更新正确的major_id，应当成功
        String majorId = UT.randomKey(KB.kb_major);
        body.put("major_id", majorId);
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_id"), majorId);
        Assert.assertEquals(parseJson(data, "major_name"), KB.kb_major.get(majorId));

        // 更新错误的major_id，应当不成功
        body.replace("major_id", "1000000");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_id"), majorId);
        Assert.assertEquals(parseJson(data, "major_name"), KB.kb_major.get(majorId));

    }

    @Test(enabled = false)
    public void test_07_CRM更新医生详情_更新手机() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生手机信息");

        // 更新正确的mobile，应当成功
        String phone = UT.randomPhone();
        body.put("mobile", phone);
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "mobile"), phone);

    }

    @Test
    public void test_08_CRM更新医生详情_更新医生城市() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生城市信息");
        // 更新正确的city_id，应当成功
        String cityId = UT.randomKey(KB.kb_city);
        body.put("city_id", cityId);
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "city_id"), cityId);
        Assert.assertEquals(parseJson(data, "city"), KB.kb_city.get(cityId));

        // 更新错误的major_id，应当不成功
        body.replace("city_id", "100000000");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "错误的city_id不应该更新成功");
        res = RegisteredDoctor_Detail.Detail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "city_id"), cityId);
        Assert.assertEquals(parseJson(data, "city"), KB.kb_city.get(cityId));

    }


}