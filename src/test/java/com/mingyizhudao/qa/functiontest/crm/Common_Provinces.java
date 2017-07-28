package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class Common_Provinces extends BaseTest {
    public static final Logger logger= Logger.getLogger(Common_Counties.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/provinces";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_返回省列表_传首字母参数() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("initial", "");
        for (int i=0; i<10; i++) {
            String initial = Generator.randomString(1).toUpperCase();
            query.replace("initial", initial);
            try {
                res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
                checkResponse(res);
                Assert.assertEquals(code, "1000000");
            } catch (IOException e) {
                logger.error(e);
            }

            JSONArray provinceList = data.getJSONArray("list");
            for (int j=0; j<provinceList.size(); j++) {
                Assert.assertEquals(provinceList.getJSONObject(j).getString("initial"), initial);
                Assert.assertNotNull(provinceList.getJSONObject(j).getString("id"));
                Assert.assertNotNull(provinceList.getJSONObject(j).getString("name"));
            }
        }
    }

    @Test
    public void test_02_不传首字母_返回所有() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
        } catch (IOException e) {
            logger.error(e);
        }

        JSONArray provinceList = data.getJSONArray("list");
        logger.info("省的个数为："+provinceList.size());
        for (int j=0; j<provinceList.size(); j++) {
            Assert.assertNotNull(provinceList.getJSONObject(j).getString("initial"));
            Assert.assertNotNull(provinceList.getJSONObject(j).getString("id"));
            Assert.assertNotNull(provinceList.getJSONObject(j).getString("name"));
        }

    }
}
