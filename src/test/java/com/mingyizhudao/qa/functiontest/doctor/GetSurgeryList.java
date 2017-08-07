package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 26/4/2017.
 */
public class GetSurgeryList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getSurgeryList";

    @Test
    public void test_01_获取手术_提供有效的categoryId() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("id", "6");
        res = HttpRequest.s_SendGet(host_doc+uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories()"));
        Assert.assertNotEquals(Helper.s_ParseJson(data, "surgeryCategories():id"), "");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgeryCategories():surgery_category_id"), "6");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "surgeryCategories():name"), "");
    }

    @Test
    public void test_02_获取手术_提供无效的categoryId_ID不存在() {
        String res = "";

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("id", "600000");
        res = HttpRequest.s_SendGet(host_doc +uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories()"));
    }

    @Test
    public void test_03_获取手术_提供无效的categoryId_ID为其他字符() {
        String res = "";

        logger.info("case1: Id = abc");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("id", "abc");
        res = HttpRequest.s_SendGet(host_doc +uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210621");

        logger.info("case2: Id = abc12");
        query.replace("id", "abc12");
        res = HttpRequest.s_SendGet(host_doc +uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210621");

        logger.info("case3: Id = -1");
        query.replace("id", "-1");
        res = HttpRequest.s_SendGet(host_doc +uri, query, mainToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "2210621");
    }
}
