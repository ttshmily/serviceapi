package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

public class PaymentList extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/payments/list";

    @Test
    public void test_01_支付单总列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String id = Create.s_CreateTid(new AppointmentTask());
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(data.getString("page"));
        Assert.assertNotNull(data.getString("page_size"));
        Assert.assertEquals(data.getInt("size"), 10);
        Assert.assertNotEquals(data.getJSONArray("list").size(), 0);
    }

    @Test
    public void test_02_支付单总列表_按订单号() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String order_number = Create.s_CreateOrderNumber(new AppointmentTask());
        query.put("order_number", order_number);

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }
}
