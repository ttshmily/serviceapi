package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Generator.s_ParseJson;

/**
 * Created by dayi on 2017/7/3.
 */
public class DeleteOrderByAgent extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/orders/{orderId}";

    @Test
    public void test_01_删除订单_下级医生() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctor = s_CreateVerifiedDoctor(dp);
        String orderId = CreateOrder.s_CreateOrder(doctor.get("token"));
        int orderCountBefore = Integer.parseInt(GetOrderList_V1.s_List(doctor.get("token"), "1"));// 1 - agent
        logger.info("订单数："+orderCountBefore);
        HashMap<String,String> pathValue = new HashMap();
        pathValue.put("orderId", orderId);
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("role", "1"); // 1 - agent
        body.put("order", order);
        try {
            res = HttpRequest.s_SendDelete(host_doc+uri, body.toString(), doctor.get("token"), pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        int orderCountAfter = Integer.parseInt(GetOrderList_V1.s_List(doctor.get("token"), "1"));// 1 - agent
        logger.info("订单数："+orderCountAfter);
        Assert.assertEquals(orderCountAfter, orderCountBefore-1);
        res = GetOrderDetail_V1.s_MyInitiateOrder(doctor.get("token"), orderId);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data, "order:agent_deleted_at"));
    }

    @Test
    public void test_02_删除订单_上级医生() {
        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> doctor = s_CreateSyncedDoctor(dp);
        String orderId = CreateOrder.s_CreateOrder(mainToken);
        Order_ReceiveTask.s_ReceiveTask(orderId);
        Order_RecommendDoctor.s_RecommendDoctor(orderId, doctor.get("expert_id"));//推荐上级医生

        int orderCountBefore = Integer.parseInt(GetOrderList_V1.s_List(doctor.get("token"), "2"));// 2 - expert
        logger.info("订单数：" + orderCountBefore);
        HashMap<String,String> pathValue = new HashMap();
        pathValue.put("orderId", orderId);
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("role", "2"); // 2 - expert
        body.put("order", order);
        try {
            res = HttpRequest.s_SendDelete(host_doc+uri, body.toString(), doctor.get("token"), pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        int orderCountAfter = Integer.parseInt(GetOrderList_V1.s_List(doctor.get("token"), "2"));// 1 - agent
        logger.info("订单数：" + orderCountAfter);
        Assert.assertEquals(orderCountAfter, orderCountBefore-1);
        res = GetOrderDetail_V1.s_MyReceivedOrder(doctor.get("token"), orderId);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data, "order:surgeon_deleted_at"));
    }
}
