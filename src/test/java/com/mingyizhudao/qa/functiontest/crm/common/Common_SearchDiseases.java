package com.mingyizhudao.qa.functiontest.crm.common;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Common_SearchDiseases extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/diseases/search";

    @Test
    public void test_01_查询默认疾病列表_正常路径输入查询条件() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();

        // sub_cat_id in cat_id
        query.put("cat_id", "1");
        query.put("sub_cat_id", "5");
        query.put("name", "肿瘤");

        res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
        s_CheckResponse(res);
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
        res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
