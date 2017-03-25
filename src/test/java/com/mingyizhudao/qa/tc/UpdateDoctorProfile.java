package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.sun.tools.javac.comp.Todo;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

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
        body.body.getJSONObject("doctor").accumulate("city_id","22");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
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
        body.body.getJSONObject("doctor").accumulate("hospital_id","2");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void 已登录有token的用户可以更新个人信息inviter_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("inviter_no","SH001");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void 已登录有token的用户可以更新个人信息major_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("major_id","5");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "我想1000000");
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
        body.body.getJSONObject("doctor").accumulate("city_name", "上海");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }

    @Test
    public void 禁止更新hospital_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("hospital_name", "上海医院");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }

    @Test
    public void 禁止更新major_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("major_name", "烫伤");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }

    @Test
    public void 禁止更新inviter_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("inviter_name", "大一");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }


    @Test
    public void 单独更新非法的inviter_no字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").accumulate("inviter_no", "GF001");
        try {
            res = HttpRequest.sendPost(host+mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }
}

class DoctorProfile {

    public JSONObject body = new JSONObject();
    public DoctorProfile(boolean init) {
        JSONObject doctor = new JSONObject();
        if (init) {
            doctor.accumulate("name", "test");
            doctor.accumulate("city_id", "22");
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
        }
        this.body.accumulate("doctor",doctor);
    }

}
