package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_ModifyDetail extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_ModifyDetail.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/profiles";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_CRM更新医生详情_综合正确调用() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);

        JSONObject body = new JSONObject();
        body.put("name","大综合");
        body.put("mobile","13387654321");
        body.put("department","科室综合");
        body.put("city_id","12");
        body.put("hospital_id","12");
        body.put("major_id", "12");
        body.put("academic_title", "LECTURER");
        body.put("medical_title", "ARCHIATER");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "name"), "大综合");
        Assert.assertEquals(parseJson(data, "mobile"), "13387654321");
        Assert.assertEquals(parseJson(data, "department"), "科室综合");
        Assert.assertEquals(parseJson(data, "hospital_id"), "12");

        // 错误的医生ID，应该更新失败
        pathValue.replace("id", mainDoctorId+"1");
        try {
            res = HttpRequest.sendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_02_CRM更新医生详情_更新姓名() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();

        // 更新正确的name，应当成功
        body.put("name", "大测");
        try {
            res = HttpRequest.sendPut(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        logger.info(HttpRequest.unicodeString(res));
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "name"), "大测");
    }

    @Test
    public void test_03_CRM更新医生详情_更新所在医院() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();

        // 更新hospital_id，应当成功
        body.put("hospital_id", "4");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "hospital_id"), "4");

        // 更新hospital_name，不应当成功
        body.remove("hospital_id");
        body.put("hospital_name", "安庆市第一人民医院");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "hospital_id"), "4");

        // 更新hospital_id和hospital_name，应当以hospital_id为准。
        body.replace("hospital_name", "安庆市第一人民医院");
        body.replace("hospital_id", "5");
        try {
            res = HttpRequest.sendPut(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "hospital_id"), "5");
    }

    @Test
    public void test_04_CRM更新医生详情_更新学术职称() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", mainDoctorId);
        JSONObject body = new JSONObject();

        // 更新正确的academic_title，应当成功
        body.put("academic_title", "ASSOCIATE_PROFESSOR");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "academic_title"), "ASSOCIATE_PROFESSOR");

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
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "academic_title"), "ASSOCIATE_PROFESSOR");

    }

    @Test
    public void test_05_CRM更新医生详情_更新技术职称() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", mainDoctorId);
        JSONObject body = new JSONObject();

        // 更新正确的medical_title，应当成功
        body.put("medical_title", "ARCHIATER");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "medical_title"), "ARCHIATER");

        // 更新错误的medical_title，应当不成功
        body.replace("medical_title", "ARCHIATER_WRONG");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "medical_title"), "ARCHIATER");
    }

    @Test
    public void test_06_CRM更新医生详情_更新医生专业() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();

        // 更新正确的major_id，应当成功
        body.put("major_id", "4");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_id"), "4");

        // 更新错误的major_id，应当不成功
        body.replace("major_id", "1000");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_id"), "4");

        // 更新major_name，应当不成功
        body.remove("major_id");
        body.put("major_name", "肿瘤科");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_id"), "4");

        // 更新major_id和major_name，应当以major_id为准
        body.put("major_id", "5");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "major_id"), "4");
    }

    @Test
    public void test_07_CRM更新医生详情_更新手机() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();

        // 更新正确的mobile，应当成功
        body.put("mobile", "13312345678");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "mobile"), "13312345678");

        // 更新错误的mobile，应当失败
        body.put("mobile", "13312345678");
        try {
            res = HttpRequest.sendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_GetDetail.GetRegisteredDoctorDetail(mainDoctorId);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "mobile"), "13312345678");
    }


}
