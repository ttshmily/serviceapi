package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by dayi on 2017/6/26.
 */
public class DistrictUndelivered extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/undistributedList";

    public static String[] s_UndistributedList() {
        return new String[] {};
    }

    @Test
    public void test_01_获取待分配城市列表(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");
        JSONArray city_list = data.getJSONArray("list");
        for (int i=0; i<city_list.size(); i++) {
            JSONObject city = city_list.getJSONObject(i);
            Assert.assertEquals(city.getString("status"), "1");
        }
    }

}
