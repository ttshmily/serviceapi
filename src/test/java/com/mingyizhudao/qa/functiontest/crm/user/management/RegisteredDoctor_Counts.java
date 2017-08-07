package com.mingyizhudao.qa.functiontest.crm.user.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_Counts extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static final String uri = version+"/doctors/counts";

    @Test
    public void test_01_获取各种认证状态下医生数量() {

        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "4"); // 4种状态
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):is_verified"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):count"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(1):is_verified"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(1):count"));
    }

    @Test
    public void test_02_获取各种认证状态下医生数量_校验数据正确性() {

        String res = "";

        try {
            res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "4"); // 4种状态

        Assert.assertEquals(Integer.parseInt(Helper.s_ParseJson(data, "list(0):count")) +
                Integer.parseInt(Helper.s_ParseJson(data, "list(1):count")) +
                Integer.parseInt(Helper.s_ParseJson(data, "list(2):count")) +
                Integer.parseInt(Helper.s_ParseJson(data, "list(3):count")) , RegisteredDoctor_List.registeredDoctorList());
    }
}
