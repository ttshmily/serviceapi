package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetDiseasesInCategory extends BaseTest {

    public static final Logger logger= Logger.getLogger(GetDiseasesInCategory.class);
    public static String uri = "/api/getdiseasesincategory";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void 获取疾病_提供有效的categoryId() {
        String res = "";

        try {
            res = HttpRequest.sendGet(host+mock+uri,"id=6", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "diseases()"));
        Assert.assertNotEquals(parseJson(data, "diseases():id"), "");
        Assert.assertEquals(parseJson(data, "diseases():disease_category_id"), "6");
        Assert.assertNotEquals(parseJson(data, "diseases():name"), "");
    }

    @Test
    public void 获取疾病_提供无效的categoryId_ID不存在() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("id", "600000");
        try {
            res = HttpRequest.sendGet(host+mock+uri,"id=600000", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(parseJson(data, "diseases()"));
    }

    @Test
    public void 获取疾病_提供无效的categoryId_ID为其他字符() {
        String res = "";

        logger.info("case1: categoryId = abc");
//        HashMap<String, String> pathValue = new HashMap<String, String>();
//        pathValue.put("diseaseCategoryId", "abc");
        try {
            res = HttpRequest.sendGet(host+mock+uri,"id=abc", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210423");

        logger.info("case2: categoryId = abc12");
        try {
            res = HttpRequest.sendGet(host+mock+uri,"id=abc12", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210423");

        logger.info("case3: categoryId = -1");
        try {
            res = HttpRequest.sendGet(host+mock+uri,"id=-1", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "2210423");
    }


}
