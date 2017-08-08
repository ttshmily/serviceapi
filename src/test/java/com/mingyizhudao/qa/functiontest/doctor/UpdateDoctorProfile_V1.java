package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBHospital_Detail;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class UpdateDoctorProfile_V1 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/updatedoctorprofile";

    public static String s_Update(String token, DoctorProfile dp) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_doc+uri, dp.body.toString(), token);
        String code = Helper.s_ParseJson(JSONObject.fromObject(res), "code");
        if (code.equals("1000000")) {
            logger.info("更新医生信息成功");
        } else {
            logger.debug(Helper.unicodeString(res));
            logger.error("更新医生信息失败");
        }
        return res;
    }

    @Test(enabled = false)
    public void test_01_已登录有token的用户可以更新个人信息city_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("city_id", Generator.randomKey(KnowledgeBase.kb_city));
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void test_02_已登录有token的用户可以更新个人信息department() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("department","尿不出来科");
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:department"), "尿不出来科");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void test_03_已登录有token的用户可以更新个人信息hospital_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        String key = Generator.randomHospitalId();
        body.body.getJSONObject("doctor").put("hospital_id", key);
        HashMap<String, String> hospitalInfo = KBHospital_Detail.s_Detail(key);
        String cityId = hospitalInfo.get("city_id");
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:hospital_id"), key);

    }

    @Test (enabled = false)
    public void test_04_已登录有token的用户可以更新个人信息inviter_no() {
        String res = "";

        String key = Generator.randomEmployeeId();

        JSONObject doctor = new JSONObject();
        doctor.put("inviter_no", key);

        JSONObject dp = new JSONObject();
        dp.put("doctor", doctor);

        for( int i=0; i<10; i++) {
            key = Generator.randomEmployeeId();
            doctor.replace("inviter_no", key);
            dp.replace("doctor", doctor);
            res = HttpRequest.s_SendPost(host_doc + uri, dp.toString(), mainToken);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile_V1.s_MyProfile(mainToken);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(Helper.s_ParseJson(data, "doctor:inviter_no"), key);
        }

    }

    @Test(enabled = false)
    public void test_05_已登录有token的用户可以更新个人信息major_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        String key = Generator.randomKey(KnowledgeBase.kb_major);
        body.body.getJSONObject("doctor").put("major_id",key);
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:major_id"), key);
    }

    @Test
    public void test_06_未登录没有token的用户不可以更新信息() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void test_07_错误token的不可以更新信息并返回正确错误码() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), "niyaowoa");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1211012");
    }

    @Test(enabled = false)
    public void test_08_禁止更新city_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("city_name", "城市");
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:city_name"), "城市", "城市名称不应该改变");
    }

    @Test(enabled = false)
    public void test_09_禁止更新hospital_name字段() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        dp.body.getJSONObject("doctor").replace("hospital_name", "测试医院");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:hospital_name"), "测试医院", "医院名称不应该改变");

        logger.info("同时传入major_id和hospital_name");
        dp.body.getJSONObject("doctor").replace("hospital_name", "测试医院");
        dp.body.getJSONObject("doctor").replace("major_id", "8");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:major_id"), "8");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:hospital_name"), "测试医院", "医院名称不应该改变");

        logger.info("同时传入inviter_no和hospital_name");
        dp.body.getJSONObject("doctor").replace("hospital_name", "测试医院");
        dp.body.getJSONObject("doctor").replace("inviter_no", "SH0003");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
//        Assert.assertEquals(s_ParseJson(data, "doctor:inviter_no"), "SH0003");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:hospital_name"), "测试医院", "医院名称不应该改变");

        logger.info("同时传入name和hospital_name");
        dp.body.getJSONObject("doctor").replace("name","大一测试名称");
        dp.body.getJSONObject("doctor").replace("hospital_name","测试医院");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:name"), "大一测试名称");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:hospital_name"), "测试医院", "专业名称不应该改变");
    }

    @Test
    public void test_10_禁止更新major_name字段() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        dp.body.getJSONObject("doctor").put("major_name", "测试专业");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

        logger.info("同时传入hospital_id和major_name");
        dp.body.getJSONObject("doctor").replace("hospital_id","2");
        dp.body.getJSONObject("doctor").replace("major_name","test_major");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:hospital_id"), "2");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

        logger.info("同时传入inviter_no和major_name");
        dp.body.getJSONObject("doctor").replace("inviter_no","SH0002");
        dp.body.getJSONObject("doctor").replace("major_name","test_major");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
//        Assert.assertEquals(s_ParseJson(data, "doctor:inviter_no"), "SH0002");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

        logger.info("同时传入name和major_name");
        dp.body.getJSONObject("doctor").replace("name","大一测试名称");
        dp.body.getJSONObject("doctor").replace("major_name","test_major");
        res = HttpRequest.s_SendPost(host_doc + uri, dp.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:name"), "大一测试名称");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

    }

    @Test (enabled = false)
    public void test_11_禁止更新inviter_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("inviter_name", "大一");
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(Helper.s_ParseJson(data, "doctor:inviter_name"), "大一", "地推名称不应该改变");
    }

    @Test
    public void test_12_更新doctor_card_pictures字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("doctor_card_pictures", JSONArray.fromObject("[{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'3'}]"));
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg", "key值错误");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):type"), "3", "type值错误");
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):url"), "url值错误");
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):large_url"), "large_url缺少");
    }

    @Test
    public void test_13_更新擅长exp_list() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("exp_list", JSONArray.fromObject("[{\"category\": {\"id\": 6,\"name\": \"皮肤肿瘤\"},\"disease_list\": [{\"id\": 339,\"name\": \"早期乳腺癌\"},{\"id\": 336,\"name\": \"炎性乳腺癌\"}]},{\"category\": {\"id\": 7,\"name\": \"神经肿瘤\"},\"disease_list\": [{\"id\": 394,\"name\": \"垂体腺瘤\"},{\"id\": 393,\"name\": \"催乳素瘤\"}]}]"));
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:exp_list()"), "2");
    }


}

