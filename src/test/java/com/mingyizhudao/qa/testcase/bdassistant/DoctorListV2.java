package com.mingyizhudao.qa.testcase.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/6/23.
 */
public class DoctorListV2 extends BaseTest {
    public static final Logger logger= Logger.getLogger(DoctorListV2.class);
    public static String uri = "/api/v1/doctor/doctorList";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    @Test
    public void test_01_没有token或token错误无权限使用接口() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");

        try {
            res = HttpRequest.sendGet(host_bda + uri, query, "aaa");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "token错误不应该调用成功");
    }

    @Test
    public void test_02_登录用户无权限使用接口() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent_contact_id", mainBD);

        query.put("city_id", UT.randomCityId());
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");
    }

}
