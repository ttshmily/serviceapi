package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class UpdateDoctorProfile extends BaseTest {

    public static final Logger logger= Logger.getLogger(UpdateDoctorProfile.class);
    public static String uri = "/api/updatedoctorprofile";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";



    public static String updateDoctorProfile(String token, DoctorProfile dp) {
        String res = "";
//        DoctorProfile dp = new DoctorProfile(true);
//
//        if (map != null) {
//            for (String key : map.keySet()
//                    ) {
//                if (dp.body.getJSONObject("doctor").containsKey(key)) {
//                    dp.body.getJSONObject("doctor").replace(key, map.get(key));
//                } else {
//                    dp.body.getJSONObject("doctor").accumulate(key, map.get(key));
//                }
//            }
//        }
        try {
            res = HttpRequest.sendPost(host_doc+uri, dp.body.toString(), token);
        } catch (IOException e) {
            logger.error(e);
        }
        String code = parseJson(JSONObject.fromObject(res), "code");
        if (code.equals("1000000")) {
            logger.info("更新医生信息成功");
        } else {
            logger.error("更新医生信息失败");
        }
        return res;
    }

    @Test
    public void test_01_已登录有token的用户可以更新个人信息city_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("city_id", UT.randomKey(KB.kb_city));
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "我想1000000");
    }

    @Test
    public void test_02_已登录有token的用户可以更新个人信息department() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("department","尿不出来科");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
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
    public void test_03_已登录有token的用户可以更新个人信息hospital_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        String key = UT.randomKey(KB.kb_hospital);
        body.body.getJSONObject("doctor").put("hospital_id", key);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:hospital_id"), key);
        } catch (IOException e) {
            logger.error(e);
        }
        //TODO
    }

    @Test
    public void test_04_已登录有token的用户可以更新个人信息inviter_id() {
        String res = "";

        String key = UT.randomEmployeeId();

        JSONObject doctor = new JSONObject();
        doctor.put("inviter_no", key);

        JSONObject dp = new JSONObject();
        dp.put("doctor", doctor);

        for( int i=0; i<10; i++) {
            key = UT.randomEmployeeId();
            doctor.replace("inviter_no", key);
            dp.replace("doctor", doctor);
            try {
                res = HttpRequest.sendPost(host_doc + uri, dp.toString(), mainToken);
                checkResponse(res);
                Assert.assertEquals(code, "1000000");
                res = GetDoctorProfile.getDoctorProfile(mainToken);
                checkResponse(res);
                Assert.assertEquals(code, "1000000");
                Assert.assertEquals(parseJson(data, "doctor:inviter_no"), key);
            } catch (IOException e) {
                logger.error(e);
            }
        }

    }

    @Test
    public void test_05_已登录有token的用户可以更新个人信息major_id() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        String key = UT.randomKey(KB.kb_major);
        body.body.getJSONObject("doctor").put("major_id",key);
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.body.toString(), mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            res = GetDoctorProfile.getDoctorProfile(mainToken);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(parseJson(data, "doctor:major_id"), key);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Test
    public void test_06_未登录没有token的用户不可以更新信息() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        try {
            res = HttpRequest.sendPost(host_doc +uri, body.body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void test_07_错误token的不可以更新信息并返回正确错误码() {
        String res = "";
        DoctorProfile body = new DoctorProfile(true);
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), "niyaowoa");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1211012");
    }

    @Test
    public void test_08_禁止更新city_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("city_name", "城市");
        try {
            res = HttpRequest.sendPost(host_doc+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data, "doctor:city_name"), "城市", "城市名称不应该改变");
    }

    @Test
    public void test_09_禁止更新hospital_name字段() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        dp.body.getJSONObject("doctor").replace("hospital_name", "测试医院");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data, "doctor:hospital_name"), "测试医院", "医院名称不应该改变");

        logger.info("同时传入major_id和hospital_name");
        dp.body.getJSONObject("doctor").replace("hospital_name", "测试医院");
        dp.body.getJSONObject("doctor").replace("major_id", "8");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "doctor:major_id"), "8");
        Assert.assertNotEquals(parseJson(data, "doctor:hospital_name"), "测试医院", "医院名称不应该改变");

        logger.info("同时传入inviter_no和hospital_name");
        dp.body.getJSONObject("doctor").replace("hospital_name", "测试医院");
        dp.body.getJSONObject("doctor").replace("inviter_no", "SH0003");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "doctor:inviter_no"), "SH0003");
        Assert.assertNotEquals(parseJson(data, "doctor:hospital_name"), "测试医院", "医院名称不应该改变");

        logger.info("同时传入name和hospital_name");
        dp.body.getJSONObject("doctor").replace("name","大一测试名称");
        dp.body.getJSONObject("doctor").replace("hospital_name","测试医院");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);

        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "doctor:name"), "大一测试名称");
        Assert.assertNotEquals(parseJson(data, "doctor:hospital_name"), "测试医院", "专业名称不应该改变");
    }

    @Test
    public void test_10_禁止更新major_name字段() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        dp.body.getJSONObject("doctor").put("major_name", "测试专业");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

        logger.info("同时传入hospital_id和major_name");
        dp.body.getJSONObject("doctor").replace("hospital_id","2");
        dp.body.getJSONObject("doctor").replace("major_name","test_major");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);

        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "doctor:hospital_id"), "2");
        Assert.assertNotEquals(parseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

        logger.info("同时传入inviter_no和major_name");
        dp.body.getJSONObject("doctor").replace("inviter_no","SH0002");
        dp.body.getJSONObject("doctor").replace("major_name","test_major");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);

        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "doctor:inviter_no"), "SH0002");
        Assert.assertNotEquals(parseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

        logger.info("同时传入name和major_name");
        dp.body.getJSONObject("doctor").replace("name","大一测试名称");
        dp.body.getJSONObject("doctor").replace("major_name","test_major");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, dp.body.toString(), mainToken);

        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(parseJson(data, "doctor:name"), "大一测试名称");
        Assert.assertNotEquals(parseJson(data, "doctor:major_name"), "测试专业", "专业名称不应该改变");

    }

    @Test
    public void test_11_禁止更新inviter_name字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("inviter_name", "大一");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data, "doctor:inviter_name"), "大一", "地推名称不应该改变");
    }


    @Test
    public void test_12_单独更新非法的inviter_no字段() {
        String res = "";
        DoctorProfile body = new DoctorProfile(false);
        body.body.getJSONObject("doctor").put("inviter_no", "GF001");
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.body.toString(), mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210317");
    }

}

