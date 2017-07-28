package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 16/5/2017.
 */
public class KBExpert_Detail extends BaseTest {
    public static final Logger logger= Logger.getLogger(KBExpert_Detail.class);
    public static String uri = "/api/v1/medicallibrary/doctors/{id}";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static String Detail(String expertId) {
        String res = "";
        if (expertId == null) return null;
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",expertId);
        try {
            res = HttpRequest.s_SendGet(host_crm+uri,"", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取医库医生详情_有效ID() {

        String res = "";
        ExpertProfile ep = new ExpertProfile(true);
        HashMap<String, String> info = KBExpert_Create.Create(ep);
        if (info == null) Assert.fail("创建医库医生失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id",info.get("id"));
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
        Assert.assertEquals(Generator.parseJson(data, "academic_title_list"), ep.body.getString("academic_title_list"));
        Assert.assertEquals(Generator.parseJson(data, "gender"), ep.body.getString("gender"));
        Assert.assertEquals(Generator.parseJson(data, "name"), ep.body.getString("name"));
        Assert.assertEquals(Generator.parseJson(data, "birthday"), ep.body.getString("birthday").replace('/', '-'));
        Assert.assertEquals(Generator.parseJson(data, "major_id"), ep.body.getString("major_id"));
        Assert.assertEquals(Generator.parseJson(data, "hospital_name"), KnowledgeBase.kb_hospital.get(ep.body.getString("hospital_id")));
        Assert.assertEquals(Generator.parseJson(data, "honour"), ep.body.getString("honour"));
        String expert_city_id = Generator.parseJson(data, "city_id");
        HashMap<String, String> hospitalInfo = KBHospital_Detail.Detail(ep.body.getString("hospital_id"));

        String hospital_city_id = hospitalInfo.get("city_id");
        Assert.assertEquals(hospital_city_id, expert_city_id);

        Assert.assertEquals(Generator.parseJson(data, "certified_status"), "NOT_CERTIFIED");
        Assert.assertEquals(Generator.parseJson(data, "signed_status"), "NOT_SIGNED");
        Assert.assertEquals(Generator.parseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
        Assert.assertEquals(Generator.parseJson(data, "medical_title_list"), ep.body.getString("medical_title_list"));
    }

    @Test
    public void test_02_获取医库医生详情_无效ID() {

        String res = "";
        HashMap<String, String> info = KBExpert_Create.Create(new ExpertProfile(true));
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", "111"+info.get("id"));
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
