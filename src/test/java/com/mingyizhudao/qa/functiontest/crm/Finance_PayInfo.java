package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/3.
 */
public class Finance_PayInfo extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/finances/pays/{paymentNumber}";

    @Test
    public void test_01_获取支付对象的相关信息() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("paymentNumber", "");
        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

    }
}
