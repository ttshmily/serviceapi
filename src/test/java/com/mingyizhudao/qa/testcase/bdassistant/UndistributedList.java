package com.mingyizhudao.qa.testcase.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/6/26.
 */
public class UndistributedList extends BaseTest {
    public static final Logger logger= Logger.getLogger(UndistributedList.class);
    public static String uri = "/api/v1/user/undistributedList";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取待分配城市列表(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "");
    }
}
