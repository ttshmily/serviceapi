package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.testcase.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static com.mingyizhudao.qa.util.UT.parseJson;

/**
 * Created by dayi on 2017/7/3.
 */
public class DeleteOrderByAgent extends BaseTest {

    public static final Logger logger= Logger.getLogger(DeleteOrderByAgent.class);
    public static String uri = "/api/orders/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_删除订单_下级医生() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctor = CreateVerifiedDoctor(dp);
        String orderId = CreateOrder.CreateOrder(doctor.get("token"));
        int orderCountBefore = Integer.parseInt(GetOrderList_V1.List(doctor.get("token"), "1"));// 1 - agent
        logger.info("订单数："+orderCountBefore);
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
        int orderCountAfter = Integer.parseInt(GetOrderList_V1.List(doctor.get("token"), "1"));// 1 - agent
        logger.info("订单数："+orderCountAfter);
        Assert.assertEquals(orderCountAfter, orderCountBefore-1);
        res = GetOrderDetail_V1.MyInitiateOrder(doctor.get("token"), orderId);
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "order:agent_deleted_at"));
    }

    @Test
    public void test_02_删除订单_上级医生() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctor = CreateSyncedDoctor(dp);
        String orderId = CreateOrder.CreateOrder(mainToken);
        Order_ReceiveTask.receiveTask(orderId);
        Order_RecommendDoctor.recommendDoctor(orderId, doctor.get("expert_id"));//推荐上级医生

        int orderCountBefore = Integer.parseInt(GetOrderList_V1.List(doctor.get("token"), "2"));// 2 - expert
        logger.info("订单数：" + orderCountBefore);
        HashMap<String,String> pathValue = new HashMap();
        pathValue.put("orderId", orderId);
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("role", "2"); // 2 - expert
        body.put("order", order);
        try {
            res = HttpRequest.sendDelete(host_doc+uri, body.toString(), doctor.get("token"), pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        int orderCountAfter = Integer.parseInt(GetOrderList_V1.List(doctor.get("token"), "2"));// 1 - agent
        logger.info("订单数：" + orderCountAfter);
        Assert.assertEquals(orderCountAfter, orderCountBefore-1);
        res = GetOrderDetail_V1.MyReceivedOrder(doctor.get("token"), orderId);
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "order:surgeon_deleted_at"));
    }
}
