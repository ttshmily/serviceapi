package com.mingyizhudao.qa.functiontest.crm.common;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class Common_Provinces extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/provinces";

    @Test
    public void test_01_返回省列表_传首字母参数() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("initial", "");
        for (int i=0; i<10; i++) {
            String initial = Generator.randomString(1).toUpperCase();
            query.replace("initial", initial);
            res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");

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
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        JSONArray provinceList = data.getJSONArray("list");
        logger.info("省的个数为："+provinceList.size());
        for (int j=0; j<provinceList.size(); j++) {
            Assert.assertNotNull(provinceList.getJSONObject(j).getString("initial"));
            Assert.assertNotNull(provinceList.getJSONObject(j).getString("id"));
            Assert.assertNotNull(provinceList.getJSONObject(j).getString("name"));
        }

    }
}
