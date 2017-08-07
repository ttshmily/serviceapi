package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetDiseasesInCategory extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getdiseasesincategory";

    @Test
    public void test_01_获取疾病_提供有效的categoryId() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("id", "6");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseases()"));
        Assert.assertNotEquals(Helper.s_ParseJson(data, "diseases():id"), "");
//        Assert.assertEquals(s_ParseJson(data, "diseases():disease_category_id"), "6");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "diseases():name"), "");
    }

    @Test
    public void test_02_获取疾病_提供无效的categoryId_ID不存在() {
        String res = "";

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("id", "600000");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseases()"));
    }

    @Test
    public void test_03_获取疾病_提供无效的categoryId_ID为其他字符() {
        String res = "";

        logger.info("case1: categoryId = abc");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("id", "abc");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210423");

        logger.info("case2: categoryId = abc12");
        query.replace("id", "abc12");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210423");

        logger.info("case3: categoryId = -1");
        query.replace("id", "-1");
        res = HttpRequest.s_SendGet(host_doc + uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210423");
    }

}
