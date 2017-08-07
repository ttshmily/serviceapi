package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 17/5/2017.
 */
public class TeamList extends BaseTest {

    public static final Logger logger= Logger.getLogger(TeamList.class);
    public static String uri = "/api/v1/user/teamList";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_未登录用户无权限使用接口() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, map, "", null);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");
    }

    @Test
    public void test_02_普通登录用户_返回空() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token_staff);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "非主管用户能调用teamList接口");
    }

    @Test
    public void test_03_主管用户_返回团队成员基本信息() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, map, bda_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "主管用户不能调用teamList接口");
    }

    public void test_04_主管用户_分页逻辑() {

    }

}
