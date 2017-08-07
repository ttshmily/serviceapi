package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_TrackList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/{id}/history";

    public static String s_TrackList(String doctorId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", doctorId);
        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取医生操作记录_认证成功() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = s_CreateRegisteredDoctor(dp).get("id"); // create an order
        res = s_TrackList(doctorId);
        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "2");
        res = s_TrackList(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "4");// 认证和同步，2条记录

        int id1 = Integer.parseInt(Helper.s_ParseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(Helper.s_ParseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(Helper.s_ParseJson(data, "list(2):id"));

        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
    }

    @Test
    public void test_02_获取医生操作记录_认证失败() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        DoctorProfile dp = new DoctorProfile(true);
        String doctorId = s_CreateRegisteredDoctor(dp).get("id"); // create an order
        res = s_TrackList(doctorId);
        logger.debug(res);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "2");
        res = s_TrackList(doctorId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "3");

        int id1 = Integer.parseInt(Helper.s_ParseJson(data, "list(0):id"));
        int id2 = Integer.parseInt(Helper.s_ParseJson(data, "list(1):id"));
        int id3 = Integer.parseInt(Helper.s_ParseJson(data, "list(2):id"));

        Assert.assertTrue(id1 > id2, "没有倒序排列");
        Assert.assertTrue(id2 > id3, "没有倒序排列");
    }

}
