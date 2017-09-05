package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Specialty;
import com.mingyizhudao.qa.dataprofile.User;
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

import java.util.ArrayList;
import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail.s_Detail;
import static com.mingyizhudao.qa.utilities.Generator.*;

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

//    public static String s_Update(String token, DoctorProfile_Test dp) {
//        String res = "";
//        TestLogger logger = new TestLogger(s_JobName());
//        res = HttpRequest.s_SendPost(host_doc+uri, dp.body.toString(), token);
//        String code = Helper.s_ParseJson(JSONObject.fromObject(res), "code");
//        if (code.equals("1000000")) {
//            logger.info("更新医生信息成功");
//        } else {
//            logger.debug(Helper.unicodeString(res));
//            logger.error("更新医生信息失败");
//        }
//        return res;
//    }

    public static String s_Update(String token, User user) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_doc+uri, JSONObject.fromObject(user).toString(), token);
        String code = Helper.s_ParseJson(JSONObject.fromObject(res), "code");
        if (!code.equals("1000000")) {
            logger.error(Helper.unicodeString(res));
            logger.error("更新医生信息失败");
        }
        return res;
    }

/*    @Test(enabled = false)
    public void test_01_已登录有token的用户可以更新个人信息city_id() {
        String res = "";
        User body = new User();
        body.body.getJSONObject("doctor").put("city_id", Generator.randomKey(KnowledgeBase.kb_city));
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "我想1000000");
    }*/

    @Test
    public void test_02_已登录有token的用户可以更新个人信息department() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String tmpToken = doc.get("token");
        dp.getDoctor().setDepartment("科室");
        res = HttpRequest.s_SendPost(host_doc + uri, JSONObject.fromObject(dp).toString(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:department"), dp.getDoctor().getDepartment());
    }

    @Test
    public void test_03_已登录有token的用户可以更新个人信息hospital_id() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String tmpToken = doc.get("token");
        String expertId = doc.get("expert_id");

        String hospitalId = randomHospitalId();
        res = KBHospital_Detail.s_Detail(hospitalId);
        s_CheckResponse(res);
        String cityId = data.getString("city_id");

        dp.getDoctor().setHospital_id(hospitalId);
        res = HttpRequest.s_SendPost(host_doc + uri, JSONObject.fromObject(dp).toString(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:hospital_id"), dp.getDoctor().getHospital_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:hospital_name"), hospitalName(dp.getDoctor().getHospital_id()));
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:city_id"), cityId);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:city"), cityName(cityId));

//        res = s_Detail(expertId);
//        s_CheckResponse(res);
//        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_id"), dp.getDoctor().getHospital_id());
//        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_name"), hospitalName(dp.getDoctor().getHospital_id()));
//        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), cityId);
//        Assert.assertEquals(Helper.s_ParseJson(data, "city_name"), cityName(cityId));
    }

/*    @Test (enabled = false)
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

    }*/

/*    @Test(enabled = false)
    public void test_05_已登录有token的用户可以更新个人信息major_id() {
        String res = "";
        DoctorProfile_Test body = new DoctorProfile_Test(false);
        String key = Generator.randomKey(KnowledgeBase.kb_major);
        body.body.getJSONObject("doctor").put("major_id",key);
        res = HttpRequest.s_SendPost(host_doc + uri, body.body.toString(), mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:major_id"), key);
    }*/

    @Test
    public void test_06_未登录没有token的用户不可以更新信息() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String tmpToken = doc.get("token");

        res = HttpRequest.s_SendPost(host_doc + uri, JSONObject.fromObject(dp).toString(), "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void test_07_错误token的不可以更新信息并返回正确错误码() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String tmpToken = doc.get("token");

        res = HttpRequest.s_SendPost(host_doc + uri, JSONObject.fromObject(dp).toString(), tmpToken+"1");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_12_更新doctor_card_pictures字段() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String tmpToken = doc.get("token");

        dp.getDoctor().setDoctor_card_pictures(new ArrayList<User.UserDetail.Picture>(){
            {
                add(dp.getDoctor().new Picture("2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg", "3"));
            }
        });
        res = HttpRequest.s_SendPost(host_doc + uri, JSONObject.fromObject(dp).toString(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):key"), dp.getDoctor().getDoctor_card_pictures().get(0).getKey(), "key值错误");
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):type"), dp.getDoctor().getDoctor_card_pictures().get(0).getType(), "type值错误");
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):url"), "url值错误");
        Assert.assertNotNull(Helper.s_ParseJson(data, "doctor:doctor_card_pictures(0):large_url"), "large_url缺少");
    }

    @Test
    public void test_13_更新擅长exp_list_并同步医库() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateSyncedDoctor(dp);
        String tmpToken = doc.get("token");
        String expertId = doc.get("expert_id");
        dp.getDoctor().setExp_list(new ArrayList<Specialty>(){
            {
                int size = (int)randomInt(4);
                for (int i = 0; i < size; i++) {
                    add(new Specialty());
                }
            }
        });
        res = HttpRequest.s_SendPost(host_doc + uri, dp.transform(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:exp_list()"), String.valueOf(dp.getDoctor().getExp_list().size()));

        res = s_Detail(expertId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "specialty_list()"), String.valueOf(dp.getDoctor().getExp_list().size()));
    }

    @Test
    public void test_14_更新擅长exp_list_同步到医库医生() {
        String res = "";
        User dp = new User();
        HashMap<String, String> doc = s_CreateVerifiedDoctor(dp);
        String tmpToken = doc.get("token");

        dp.getDoctor().setExp_list(new ArrayList<Specialty>(){
            {
                int size = (int)randomInt(4);
                for (int i = 0; i < size; i++) {
                    add(new Specialty());
                }
            }
        });
        res = HttpRequest.s_SendPost(host_doc + uri, dp.transform(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = GetDoctorProfile_V1.s_MyProfile(tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "doctor:exp_list()"), String.valueOf(dp.getDoctor().getExp_list().size()));
    }
}

