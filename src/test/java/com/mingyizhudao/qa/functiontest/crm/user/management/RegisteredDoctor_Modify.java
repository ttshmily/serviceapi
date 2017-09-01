package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBHospital_Detail;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_Modify extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/profiles";
    public static String mock = false ? "/mockjs/1" : "";

    public static String s_Modify(String doctorId, JSONObject map) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",doctorId);
        if (map == null) return null;
        if (map.keySet().size() != 0) {
            res = HttpRequest.s_SendPut(host_crm+uri, map.toString(), crm_token, pathValue);
        }
        return res;
    }

    @Test
    public void test_01_CRM更新医生详情_综合正确调用() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);

        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生信息");
        body.put("department","科室综合");
        String city = Generator.randomCityId();
        String hospital = Generator.randomHospitalId();
        String major = Generator.randomMajorId();
        String academic = Generator.randomAcademicId();
        String medical = Generator.randomMedicalId();
        body.put("city_id",city);
        body.put("hospital_id",hospital);
        body.put("major_id", major);
        body.put("academic_title", academic);
        body.put("medical_title", medical);

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospital);
        String another_city = hospitalInfo.get("city_id");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        logger.info(Helper.unicodeString(res));
        s_CheckResponse(res);
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
//        Assert.assertEquals(s_ParseJson(data, "department"), "科室综合");
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospital);
        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), another_city);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), major);
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), academic);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), medical);

        // 错误的医生ID，应该更新失败
        pathValue.replace("id", mainDoctorId+"11111");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
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
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "name"), "美女医生");
    }

    @Test
    public void test_03_CRM更新医生详情_更新所在医院() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生医院信息");

        // 更新hospital_id，应当成功
        String hospitalId = Generator.randomHospitalId();
        body.put("hospital_id", hospitalId);

        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(hospitalId);
        String another_city = hospitalInfo.get("city_id");

        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), Generator.hospitalName(hospitalId));
        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), another_city);
        Assert.assertEquals(Helper.s_ParseJson(data, "city"), Generator.cityName(another_city));

        // 更新hospital_id和hospital_name，应当以hospital_id为准。
        hospitalId = Generator.randomHospitalId();
        body.replace("hospital_id", hospitalId);
        body.put("hospital_name", "测试医院");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), hospitalId);
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), Generator.hospitalName(hospitalId));
    }

    @Test
    public void test_04_CRM更新医生详情_更新学术职称() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生学术职称信息");

        // 更新正确的academic_title，应当成功
        String academic = Generator.randomKey(KnowledgeBase.kb_academic_title);
        body.put("academic_title", academic);
        res = HttpRequest.s_SendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), academic);
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title"), KnowledgeBase.kb_academic_title.get(academic));

        // 更新错误的academic_title，应当不成功
        body.replace("academic_title", "ASSOCIATE_PROFESSOR_WRONG");
        res = HttpRequest.s_SendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "academic_title_list"), academic);

    }

    @Test
    public void test_05_CRM更新医生详情_更新技术职称() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生技术职称信息");

        // 更新正确的medical_title，应当成功
        String medical = Generator.randomKey(KnowledgeBase.kb_medical_title);
        body.put("medical_title", medical);
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), medical);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title"), KnowledgeBase.kb_medical_title.get(medical));

        // 更新错误的medical_title，应当不成功
        body.replace("medical_title", "ARCHIATER_WRONG");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title_list"), medical);
        Assert.assertEquals(Helper.s_ParseJson(data, "medical_title"), KnowledgeBase.kb_medical_title.get(medical));
    }

    @Test
    public void test_06_CRM更新医生详情_更新医生专业() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生专业信息");

        // 更新正确的major_id，应当成功
        String majorId = Generator.randomKey(KnowledgeBase.kb_major);
        body.put("major_id", majorId);
        res = HttpRequest.s_SendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), majorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_name"), KnowledgeBase.kb_major.get(majorId));

        // 更新错误的major_id，应当不成功
        body.replace("major_id", "1000000");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_id"), majorId);
        Assert.assertEquals(Helper.s_ParseJson(data, "major_name"), KnowledgeBase.kb_major.get(majorId));

    }

    @Test(enabled = false)
    public void test_07_CRM更新医生详情_更新手机() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生手机信息");

        // 更新正确的mobile，应当成功
        String phone = Generator.randomPhone();
        body.put("mobile", phone);
        res = HttpRequest.s_SendPut(host_crm+mock+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "mobile"), phone);

    }

    @Test
    public void test_08_CRM更新医生详情_更新医生工牌照() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",mainDoctorId);
        JSONObject body = new JSONObject();
        body.put("content", "自动化修改医生工牌照信息");

        body.accumulate("doctor_card_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'3'}").toString());
        body.accumulate("doctor_card_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102738.jpg';'type':'3'}").toString());
        body.accumulate("doctor_card_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102739.jpg';'type':'3'}").toString());
        body.accumulate("doctor_card_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102736.jpg';'type':'3'}").toString());
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_card_pictures"));
        int actual_size = Integer.parseInt(Helper.s_ParseJson(data, "doctor_card_pictures()"));
        Assert.assertEquals(actual_size, body.getJSONArray("doctor_card_pictures").size());
        for (int i=0; i<actual_size; i++) {
            Assert.assertEquals(Helper.s_ParseJson(data, "doctor_card_pictures("+i+"):key"), body.getJSONArray("doctor_card_pictures").getJSONObject(i).getString("key"));
        }


        body.accumulate("doctor_card_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102740.jpg';'type':'3'}").toString());
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_card_pictures"));
        actual_size = Integer.parseInt(Helper.s_ParseJson(data, "doctor_card_pictures()"));
        Assert.assertEquals(actual_size, body.getJSONArray("doctor_card_pictures").size());
        for (int i=0; i<actual_size; i++) {
            Assert.assertEquals(Helper.s_ParseJson(data, "doctor_card_pictures("+i+"):key"), body.getJSONArray("doctor_card_pictures").getJSONObject(i).getString("key"));
        }

        body.accumulate("doctor_card_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102741.jpg';'type':'3'}").toString());
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_card_pictures"));
        actual_size = Integer.parseInt(Helper.s_ParseJson(data, "doctor_card_pictures()"));
        Assert.assertEquals(actual_size, body.getJSONArray("doctor_card_pictures").size());
        for (int i=0; i<actual_size; i++) {
            Assert.assertEquals(Helper.s_ParseJson(data, "doctor_card_pictures("+i+"):key"), body.getJSONArray("doctor_card_pictures").getJSONObject(i).getString("key"));
        }
// 删除所有图片
        body.replace("doctor_card_pictures", "[]");
        res = HttpRequest.s_SendPut(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = RegisteredDoctor_Detail.s_Detail(mainDoctorId);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor_card_pictures_deleted"));
        Assert.assertEquals(body.getJSONArray("doctor_card_pictures").size(),0);
//        for (int i=0; i<actual_size; i++) {
//            Assert.assertEquals(UT.s_ParseJson(data, "doctor_card_pictures_deleted("+i+"):key"), body.getJSONArray("doctor_card_pictures_deleted").getJSONObject(i).getString("key"));
//        }
    }

}
