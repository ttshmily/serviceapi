package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class Common_Counties extends BaseTest {
    public static final Logger logger= Logger.getLogger(Common_Counties.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/counties";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_返回城市下区县列表() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("id", "");
        for (int i=0; i<10; i++) {
            String cityId = UT.randomCityId();
            query.replace("id", cityId);
            try {
                res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            //TODO
            JSONArray countyList = data.getJSONArray("list");
            for (int j=0; j<countyList.size(); j++) {
                Assert.assertEquals(countyList.getJSONObject(j).getString("city_id"), cityId);
                Assert.assertNotNull(countyList.getJSONObject(j).getString("id"));
                Assert.assertNotNull(countyList.getJSONObject(j).getString("name"));
            }
        }
    }

    @Test
    public void test_02_确少城市ID字段() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }
}
