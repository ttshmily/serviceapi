package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class UpdateDoctorProfile extends BaseTest {

    public static String uri = "/api/updatedoctorprofile";
    public static String mock = false ? "/mockjs/1" : "";


    public static String updateDoctorProfile() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void 已登录有token的用户可以更新个人信息city_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("city_id","22");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void 已登录有token的用户可以更新个人信息department() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("department","尿不出来科");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000", "我想1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:department"), "尿不出来科");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void 已登录有token的用户可以更新个人信息hospital_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("hospital_id","8");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:hospital_id"), "8");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Test
    public void 已登录有token的用户可以更新个人信息inviter_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("inviter_no","SH0006");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:inviter_no"), "SH0006");
        } catch (IOException e) {
            logger.error(e);
        }

    }

    @Test
    public void 已登录有token的用户可以更新个人信息major_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("major_id","5");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:major_id"), "5");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Test
    public void 未登录没有token的用户不可以更新信息() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void 错误token的不可以更新信息并返回正确错误码() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), "niyaowoa");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }

    @Test
    public void 禁止更新city_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("city_name", "上海");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210211");
    }

    @Test
    public void 禁止更新hospital_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("hospital_name", "上海医院");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        // TODO: need to check DB to verify
        Assert.assertEquals(code, "2210211");
    }

    @Test
    public void 禁止更新major_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("major_name", "烫伤");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210211");
    }

    @Test
    public void 禁止更新inviter_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("inviter_name", "大一");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210211");
        //TODO need to check DB to verify inviter_name not changed
    }


    @Test
    public void 单独更新非法的inviter_no字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("inviter_no", "GF001");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210317");
    }

    @Test
    public void 已登录有token的用户同时更新hospital_id和major_name() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").replace("hospital_id","2");
        body.body.getJSONObject("doctor").replace("major_name","22222");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:hospital_id"), "2");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        body.body.getJSONObject("doctor").replace("hospital_id","1");
        body.body.getJSONObject("doctor").replace("major_name","");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:hospital_id"), "1");
        } catch (IOException e) {
            logger.error(e);
        }


    }
}

class DoctorProfile {

    public JSONObject body = new JSONObject();
    public DoctorProfile(boolean init) {
        JSONObject doctor = new JSONObject();
        if (init) {
            doctor.accumulate("name", "test");
            doctor.accumulate("city_name", "上海");
            doctor.accumulate("department", "骨科");
            doctor.accumulate("major_id", "33");
            doctor.accumulate("major_name", "烧伤");
            doctor.accumulate("academic_title", "学习");
            doctor.accumulate("medical_title", "工程师");
            doctor.accumulate("hospital_id", "3");
            doctor.accumulate("hospital_name", "上海医院");
            doctor.accumulate("inviter_no", "GD0001");
            doctor.accumulate("inviter_name", "黄燕");
        } else {
            doctor.accumulate("name", "");
            doctor.accumulate("city_name", "");
            doctor.accumulate("department", "");
            doctor.accumulate("major_id", "");
            doctor.accumulate("major_name", "");
            doctor.accumulate("academic_title", "");
            doctor.accumulate("medical_title", "");
            doctor.accumulate("hospital_id", "");
            doctor.accumulate("hospital_name", "");
            doctor.accumulate("inviter_no", "");
            doctor.accumulate("inviter_name", "");
        }
        this.body.accumulate("doctor",doctor);
    }

}
