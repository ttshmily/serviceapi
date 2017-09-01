package com.mingyizhudao.qa.functiontest.crm.common;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class Common_UploadToken extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/Common/uploadToken";

    @Test
    public void test_01_获取上传token5() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "5");
        query.put("fileName", "test.jpg");
        query.put("fileSize", "15869");
        query.put("MIME", "JPG");
        res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "key"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "thumbnailUrl"));
    }

    @Test
    public void test_02_获取上传token4() {

        String res = "";

        HashMap<String, String> query = new HashMap<>();
        query.put("type", "4");
        query.put("fileName", "test.jpg");
        query.put("fileSize", "1356949");
        query.put("MIME", "JPG");
        res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "key"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "token"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "thumbnailUrl"));
    }
}
