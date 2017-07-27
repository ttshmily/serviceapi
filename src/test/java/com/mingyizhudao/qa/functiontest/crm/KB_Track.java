package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.crm.DiseaseProfile;
import com.mingyizhudao.qa.dataprofile.crm.ExpertProfile;
import com.mingyizhudao.qa.dataprofile.crm.HospitalProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KB_Track extends BaseTest {
    public static final Logger logger= Logger.getLogger(KB_Track.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/Common/tracks";
    public static String mock = false ? "/mockjs/1" : "";

    public static JSONArray TrackList(String type, String id) {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("type", type);
        query.put("id", id);
        try {
            res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        JSONObject node = JSONObject.fromObject(res);
        String code = node.getString("code");
        if (!code.equals("1000000")) return null;
        return node.getJSONObject("data").getJSONArray("list");
    }

    @Test
    public void test_01_获取操作记录() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String[] typeArray = new String[] {"DOCTOR","HOSPITAL","DISEASE"};
        for (String type:typeArray) {
//            logger.debug(type);
            query.put("type", type);
            try {
                res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
                checkResponse(res);
                Assert.assertEquals(code, "1000000");
            } catch (IOException e) {
                logger.error(e);
            }
            Assert.assertNotNull(Generator.parseJson(data, "list():id"));
            Assert.assertNotNull(Generator.parseJson(data, "list():operate_object_id"));
            Assert.assertNotNull(Generator.parseJson(data, "list():operate_type"));
            Assert.assertNotNull(Generator.parseJson(data, "list():operator_id"));
            Assert.assertNotNull(Generator.parseJson(data, "list():operator_name"));
            Assert.assertNotNull(Generator.parseJson(data, "list():operator_role"));
            Assert.assertNotNull(Generator.parseJson(data, "list():records"));
        }
    }

    @Test
    public void test_02_获取操作记录_传入ID() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String[] typeArray = new String[] {"DOCTOR","HOSPITAL","DISEASE"};
        for (String type:typeArray) {
            logger.debug(type);
            String id="";
            switch (type) {
                case "DOCTOR":
                    id = Generator.randomExpertId();
                    break;
                case "HOSPITAL":
                    id = Generator.randomHospitalId();
                    break;
                case "DISEASE":
                    id = Generator.randomDiseaseId();
                    break;
            }
            query.put("type", type);
            query.put("id", id);
            try {
                res = HttpRequest.sendGet(host_crm + uri, query, crm_token);

            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            JSONArray trackList = data.getJSONArray("list");
            for(int i=0; i<trackList.size(); i++) {
                JSONObject track = trackList.getJSONObject(i);
                Assert.assertEquals(track.getString("operate_object"), type);
                Assert.assertEquals(track.getString("operate_object_id"), id);
            }
        }
    }

    @Test
    public void test_03_获取医库医生操作记录() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("type", "DOCTOR");
        //创建医生
        HashMap<String,String> info = KBExpert_Create.Create(new ExpertProfile(true));
        String expertId = info.get("id");
        query.put("id", expertId);
        JSONArray trackList = TrackList("DOCTOR", expertId);
        Assert.assertEquals(trackList.size(), 1);
        KBExpert_Update.Update(expertId, new ExpertProfile(true));
        trackList = TrackList("DOCTOR", expertId);
        Assert.assertEquals(trackList.size(), 2);
        KBExpert_Update.Update(expertId, new ExpertProfile(true));
        trackList = TrackList("DOCTOR", expertId);
        Assert.assertEquals(trackList.size(), 3);
        List<String> list = new ArrayList<String>();
        list.add(Generator.randomDiseaseId());
        KBExpert_Diseases.Connect(expertId, list);
        trackList = TrackList("DOCTOR", expertId);
        Assert.assertEquals(trackList.size(), 4);
    }

    @Test
    public void test_04_获取医院操作记录() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("type", "HOSPITAL");
        //创建医院
        HashMap<String,String> info = KBHospital_Create.Create(new HospitalProfile(true));
        String hospitalId = info.get("id");
        query.put("id", hospitalId);
        JSONArray trackList = TrackList("HOSPITAL", hospitalId);
        Assert.assertEquals(trackList.size(), 1);
        KBHospital_Update.Update(hospitalId, new HospitalProfile(true));
        trackList = TrackList("HOSPITAL", hospitalId);
        Assert.assertEquals(trackList.size(), 2);
        KBHospital_Update.Update(hospitalId, new HospitalProfile(true));
        trackList = TrackList("HOSPITAL", hospitalId);
        Assert.assertEquals(trackList.size(), 3);

    }

    @Test
    public void test_05_获取疾病操作记录() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("type", "DISEASE");
        //创建疾病
        HashMap<String,String> info = KBDisease_Create.Create(new DiseaseProfile(true));
        String diseaseId = info.get("id");
        query.put("id", diseaseId);
        JSONArray trackList = TrackList("DISEASE", diseaseId);
        Assert.assertEquals(trackList.size(), 1);
        KBDisease_Update.Update(diseaseId, new DiseaseProfile(true));
        trackList = TrackList("DISEASE", diseaseId);
        Assert.assertEquals(trackList.size(), 2);
        KBDisease_Update.Update(diseaseId, new DiseaseProfile(true));
        trackList = TrackList("DISEASE", diseaseId);
        Assert.assertEquals(trackList.size(), 3);
    }
}
