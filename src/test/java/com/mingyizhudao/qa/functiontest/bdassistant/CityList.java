package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;

import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by dayi on 2017/6/26.
 */
public class CityList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/cities";

    @Test
    public void test_01_获取个人负责区域列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        for (String token: new String[] {bda_session, bda_session_staff}) {
            for (String provinceId: new String[] {"320000", "330000"}) {
                logger.info("provinceId: "+provinceId);
                query.put("provinceId", provinceId);
                res = HttpRequest.s_SendGet(host_bda + uri, query, token);
                s_CheckResponse(res);
                Assert.assertEquals(code, "1000000");
                JSONArray city_list = data.getJSONArray("list");
                for(int i=0; i<city_list.size(); i++) {
                    JSONObject city = city_list.getJSONObject(i);
                    if(city.getString("id").equals("520300")) Assert.assertNotNull(city.getString("bdname"));
                    if(city.getString("id").equals("320200")) Assert.assertNotNull(city.getString("bdname"));
                }
            }
        }
    }

}
