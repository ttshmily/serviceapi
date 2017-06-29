package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
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
        Assert.assertNotNull(UT.parseJson(data, "load_take"));
        Assert.assertNotNull(UT.parseJson(data, "handling"));
        Assert.assertNotNull(UT.parseJson(data, "wait_pay"));
        Assert.assertNotNull(UT.parseJson(data, "wait_upload_summary"));
        Assert.assertNotNull(UT.parseJson(data, "wait_verify_summary"));
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

        Assert.assertEquals(Integer.parseInt(UT.parseJson(data, "load_take")) +
                Integer.parseInt(UT.parseJson(data, "handling")) +
                Integer.parseInt(UT.parseJson(data, "wait_pay")) +
                Integer.parseInt(UT.parseJson(data, "wait_upload_summary")) +
                Integer.parseInt(UT.parseJson(data, "wait_verify_summary")) +
                Integer.parseInt(UT.parseJson(data, "finish")) +
                Integer.parseInt(UT.parseJson(data, "cancel")) +
                Integer.parseInt(UT.parseJson(data, "other")), Order_List.orderList());
    }

}
