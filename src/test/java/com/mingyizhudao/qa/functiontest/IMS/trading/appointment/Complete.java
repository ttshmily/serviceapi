package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Complete extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{id}/complete";

    public static boolean s_Complete(AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        String res = HttpRequest.s_SendPut(host_ims+uri, at.transform(), crm_token);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        //TODO
        return r.getJSONObject("data").getString("status").equals("COMPLETED") &&
                r.getJSONObject("data").getString("status").equals("5000");
    }

    @Test
    public void test_01_完成订单_受理中() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid  =Create.s_CreateOrderNumber(at);
        String orderNumber = getOrderNumberByTid(tid);
        pathValue.put("orderNumber", orderNumber);

        res = HttpRequest.s_SendPut(host_ims+uri, "", crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        Detail.s_Detail(tid);
        s_CheckResponse(res);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "COMPLETE");
    }

    @Test
    public void test_02_完成订单_服务中() {

    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }
}
