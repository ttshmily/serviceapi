package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class Common_UploadToken extends BaseTest {
    public static final Logger logger= Logger.getLogger(Common_UploadToken.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/Common/uploadToken";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取上传token5() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "5");
        query.put("fileName", "test.jpg");
        query.put("fileSize", "15869");
        query.put("MIME", "JPG");
        try {
            res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
        } catch (IOException e) {
            logger.error(e);
        }
        Assert.assertNotNull(parseJson(data, "key"));
        Assert.assertNotNull(parseJson(data, "token"));
        Assert.assertNotNull(parseJson(data, "thumbnailUrl"));
    }

    @Test
    public void test_02_获取上传token4() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "4");
        query.put("fileName", "test.jpg");
        query.put("fileSize", "1356949");
        query.put("MIME", "JPG");
        try {
            res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
        } catch (IOException e) {
            logger.error(e);
        }
        Assert.assertNotNull(parseJson(data, "key"));
        Assert.assertNotNull(parseJson(data, "token"));
        Assert.assertNotNull(parseJson(data, "thumbnailUrl"));
    }
}
