package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.crm.HospitalProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class KBHospital_Detail extends BaseTest {
    public static final Logger logger= Logger.getLogger(KBHospital_Detail.class);
    public static String uri = "/api/v1/medicallibrary/hospitals/{hospital_id}";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static String Detail(String hospitalId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        try {
            res = HttpRequest.sendGet(host_crm+uri,"", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取医院详情_有效ID() {

        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id",info.get("id"));
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(parseJson(data, "name"), hp.body.getString("name"));
        Assert.assertEquals(parseJson(data, "short_name"), hp.body.getString("short_name"));
        Assert.assertEquals(parseJson(data, "hospital_class_list"), hp.body.getString("hospital_class_list"));
        Assert.assertEquals(parseJson(data, "type_list"), hp.body.getString("type_list"));
        Assert.assertEquals(parseJson(data, "city_id"), hp.body.getString("city_id"));
        Assert.assertEquals(parseJson(data, "city_name"), UT.cityName(hp.body.getString("city_id")));
        Assert.assertEquals(parseJson(data, "county_id"), hp.body.getString("county_id"));
        Assert.assertEquals(parseJson(data, "county_name"), UT.countyName(hp.body.getString("county_id")));
        Assert.assertEquals(parseJson(data, "phone"), hp.body.getString("phone"));
        Assert.assertEquals(parseJson(data, "description"), hp.body.getString("description"));
    }

    @Test
    public void test_02_获取医院详情_无效ID() {

        String res = "";
        HospitalProfile hp = new HospitalProfile(true);
        HashMap<String, String> info = KBHospital_Create.Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", "111" + info.get("id"));
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }
}
