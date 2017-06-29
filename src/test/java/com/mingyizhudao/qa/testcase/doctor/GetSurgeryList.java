package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 26/4/2017.
 */
public class GetSurgeryList extends BaseTest {

    public static final Logger logger= Logger.getLogger(GetSurgeryList.class);
    public static String uri = "/api/getSurgeryList";

    @Test
    public void test_01_获取手术_提供有效的categoryId() {
        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("id", "6");
        try {
            res = HttpRequest.sendGet(host_doc+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(UT.parseJson(data, "surgeryCategories()"));
        Assert.assertNotEquals(UT.parseJson(data, "surgeryCategories():id"), "");
        Assert.assertEquals(UT.parseJson(data, "surgeryCategories():surgery_category_id"), "6");
        Assert.assertNotEquals(UT.parseJson(data, "surgeryCategories():name"), "");
    }

    @Test
    public void test_02_获取手术_提供无效的categoryId_ID不存在() {
        String res = "";

        HashMap<String, String> query = new HashMap<String, String>();
        query.put("id", "600000");
        try {
            res = HttpRequest.sendGet(host_doc +uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(UT.parseJson(data, "surgeryCategories()"));
    }

    @Test
    public void test_03_获取手术_提供无效的categoryId_ID为其他字符() {
        String res = "";

        logger.info("case1: Id = abc");
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("id", "abc");
        try {
            res = HttpRequest.sendGet(host_doc +uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210621");

        logger.info("case2: Id = abc12");
        query.replace("id", "abc12");
        try {
            res = HttpRequest.sendGet(host_doc +uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210621");

        logger.info("case3: Id = -1");
        query.replace("id", "-1");
        try {
            res = HttpRequest.sendGet(host_doc +uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210621");
    }
}
