package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/7/3.
 */
public class DeleteOrderByAgent extends BaseTest {

    public static final Logger logger= Logger.getLogger(DeleteOrderByAgent.class);
    public static String uri = "/api/orders/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_删除订单() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctor = CreateVerifiedDoctor(dp);
        String orderId = CreateOrder.CreateOrder(doctor.get("token"));
        String orderCountBefore = String.valueOf(GetOrderListV2.List(doctor.get("token"), "1"));// 1 - agent
        HashMap<String,String> pathValue = new HashMap();
        pathValue.put("orderId", orderId);
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("role", "1"); // 1 - agent
        body.put("order", order);
        try {
            res = HttpRequest.sendDelete(host_doc+uri, body.toString(), doctor.get("token"), pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        String orderCountAfter = String.valueOf(GetOrderListV2.List(doctor.get("token"), "1"));// 1 - agent
        Assert.assertEquals(orderCountBefore, orderCountAfter+1);
    }
}
