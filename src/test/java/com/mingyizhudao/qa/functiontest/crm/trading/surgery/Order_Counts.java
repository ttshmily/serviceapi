package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Counts extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/orderCounts";

    @Test
    public void test_01_各种状态下订单数量() {

        String res = "";

        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Generator.s_ParseJson(data, "load_take"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "handling"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "wait_pay"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "wait_upload_summary"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "wait_verify_summary"));
    }

    @Test
    public void test_02_各种状态下订单数量_校验数据正确性() {

        String res = "";

        try {
            res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(Integer.parseInt(Generator.s_ParseJson(data, "load_take")) +
                Integer.parseInt(Generator.s_ParseJson(data, "handling")) +
                Integer.parseInt(Generator.s_ParseJson(data, "wait_pay")) +
                Integer.parseInt(Generator.s_ParseJson(data, "wait_upload_summary")) +
                Integer.parseInt(Generator.s_ParseJson(data, "wait_verify_summary")) +
                Integer.parseInt(Generator.s_ParseJson(data, "finish")) +
                Integer.parseInt(Generator.s_ParseJson(data, "cancel")) +
                Integer.parseInt(Generator.s_ParseJson(data, "other")), Order_List.s_OrderList());
    }

}
