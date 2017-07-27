package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_TrackList extends BaseTest {

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_TrackList.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/history";
    public static String mock = false ? "/mockjs/1" : "";

    public static String trackList(String doctorId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_获取医生操作记录_认证成功() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id"); // create an order
        res = trackList(doctorId);
        logger.debug(res);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "list()"), "2");

        logger.info(RegisteredDoctor_CertifySync_V2.CertifyAndSync(doctorId, "1"));
        res = trackList(doctorId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "list()"), "4");// 认证和同步，2条记录

        int id1 = Integer.parseInt(Generator.parseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(Generator.parseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(Generator.parseJson(data, "list(2):id"));

        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
    }

    @Test
    public void test_02_获取医生操作记录_认证失败() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = CreateRegisteredDoctor(dp).get("id"); // create an order
        res = trackList(doctorId);
        logger.debug(res);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "list()"), "2");

        logger.info(RegisteredDoctor_CertifySync_V2.CertifyAndSync(doctorId, "-1"));
        res = trackList(doctorId);
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Generator.parseJson(data, "list()"), "3");

        int id1 = Integer.parseInt(Generator.parseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(Generator.parseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(Generator.parseJson(data, "list(2):id"));

        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
    }

}
