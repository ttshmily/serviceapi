package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Counts extends BaseTest {

    public static final Logger logger= Logger.getLogger(Order_Counts.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/orderCounts";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_各种状态下订单数量() {

        String res = "";

        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(parseJson(data, "load_take"));
        Assert.assertNotNull(parseJson(data, "handling"));
        Assert.assertNotNull(parseJson(data, "wait_pay"));
        Assert.assertNotNull(parseJson(data, "wait_upload_summary"));
        Assert.assertNotNull(parseJson(data, "wait_verify_summary"));
    }

    @Test
    public void test_02_各种状态下订单数量_校验数据正确性() {

        String res = "";

        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, "", crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(Integer.parseInt(parseJson(data, "load_take")) +
                Integer.parseInt(parseJson(data, "handling")) +
                Integer.parseInt(parseJson(data, "wait_pay")) +
                Integer.parseInt(parseJson(data, "wait_upload_summary")) +
                Integer.parseInt(parseJson(data, "wait_verify_summary")) +
                Integer.parseInt(parseJson(data, "finish")) +
                Integer.parseInt(parseJson(data, "cancel")) +
                Integer.parseInt(parseJson(data, "other")), Order_List.orderList());
    }

}
