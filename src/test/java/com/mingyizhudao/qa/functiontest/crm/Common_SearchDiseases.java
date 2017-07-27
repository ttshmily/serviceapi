package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Common_SearchDiseases extends BaseTest {
    public static final Logger logger= Logger.getLogger(Common_SearchDiseases.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/diseases/search";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_查询默认疾病列表_正常路径输入查询条件() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();

        // sub_cat_id in cat_id
        query.put("cat_id", "1");
        query.put("sub_cat_id", "5");
        query.put("name", "肿瘤");

        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

    }

    @Test
    public void test_02_查询疾病列表_不输入查询条件返回() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        // sub_cat_id in cat_id
        query.put("cat_id", "1");
        query.put("sub_cat_id", "6");
        query.put("name", "肿瘤");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
