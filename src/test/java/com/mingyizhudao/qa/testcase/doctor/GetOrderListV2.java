package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.*;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/6/27.
 */
public class GetOrderListV2 extends GetOrderList {
    public static final Logger logger= Logger.getLogger(GetOrderListV2.class);
    public static String uri = "/api/orders";
    public static String mock = false ? "/mockjs/1" : "";

    public static String List(String token, String flag) {
        // 1 - 下级医生；2 - 上级医生
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", flag);
        try {
            res = HttpRequest.sendGet(host_doc+uri, query, token);
        } catch (IOException e) {
            logger.debug(HttpRequest.unicodeString(res));
            logger.error(e);
            return null;
        }
        JSONObject orderList = JSONObject.fromObject(res).getJSONObject("data");
        return String.valueOf(orderList.getJSONArray("order").size());
    }

    public static String ListCompleted(String token, String flag) {
        // 1 - 下级医生；2 - 上级医生
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("flag",flag);
        query.put("status", "9000");
        query.put("page", "1");
        query.put("pageSize", "1");
        try {
            res = HttpRequest.sendGet(host_doc+uri, query, token);
        } catch (IOException e) {
            logger.error(e);
        }
        if (UT.parseJson(JSONObject.fromObject(res), "data:size") == "0") return null;
        return UT.parseJson(JSONObject.fromObject(res), "data:order(0):order_number");
    }

    @Test
    public void test_01_我发起的手术单_默认排序() {//已取消置底，已完成倒数第二顺位，其它会诊单按创建时间倒序混排
        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = CreateVerifiedDoctor(dp);
        String tmpToken = result.get("token");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","1"); //下级医生

        logger.info("创建订单with tmpToken");
        String orderId1 = CreateOrder.CreateOrder(tmpToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }

        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "1");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId2 = CreateOrder.CreateOrder(tmpToken);
        if (orderId2.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "2");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId3 = CreateOrder.CreateOrder(tmpToken);
        if (orderId3.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "3");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId4 = CreateOrder.CreateOrder(tmpToken);
        if (orderId4.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId1);

        logger.info("拒绝一个订单：期望其置底");
        Order_ReceiveTask.receiveTask(orderId4);
        String status = Order_Reject.rejectOrder(orderId4);
        if(!status.equals("9000")) {
            logger.error("拒绝订单失败");
            Assert.fail("拒绝订单失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);

        logger.info("拒绝一个订单：期望其置底");
        status = Order_ReceiveTask.receiveTask(orderId2);
        if(!status.equals("2000")) {
            logger.error("拒绝订单失败");
            Assert.fail("拒绝订单失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);
// 推荐专家
        status = Order_RecommendDoctor.recommendDoctor(orderId2, mainExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);
//创建支付
        status = Order_ThreewayCall.ThreewayCall(orderId2, "success");
        if(!status.equals("3000")) {
            logger.error("创建支付失败");
            Assert.fail("创建支付失败");
        }
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);

    }

    @Test
    public void test_02_我收到的手术单_默认排序() {//处理中->已完成->已取消，同一状态按接收时间倒序

        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = CreateVerifiedDoctor(dp);
        String tmpToken = result.get("token");
        String tmpExpertId = result.get("expert_id");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","2"); //上级医生

        logger.info("创建订单mainToken");
        String orderId1 = CreateOrder.CreateOrder(mainToken);
        String orderId2 = CreateOrder.CreateOrder(mainToken);
        String orderId3 = CreateOrder.CreateOrder(mainToken);
        String orderId4 = CreateOrder.CreateOrder(mainToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败");
        }

        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "0");

        logger.info("依次领取和推荐专家");
        Order_ReceiveTask.receiveTask(orderId1);
        Order_ReceiveTask.receiveTask(orderId2);
        Order_ReceiveTask.receiveTask(orderId3);
        Order_ReceiveTask.receiveTask(orderId4);
        String status = Order_RecommendDoctor.recommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        status = Order_RecommendDoctor.recommendDoctor(orderId2, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        status = Order_RecommendDoctor.recommendDoctor(orderId3, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        status = Order_RecommendDoctor.recommendDoctor(orderId4, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }

        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId1);
//回退后，上级医生信息清除，导致无法排序
        Order_ThreewayCall.ThreewayCall(orderId3,"success");
        status = Order_Rollback.Rollback(orderId3);
        if(!status.equals("2000")) {
            logger.debug(status);
            logger.error("回退订单失败"+orderId3);
            Assert.fail("回退订单失败"+orderId3);
        }
        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");//回退后，上级医生信息清除，导致无法排序
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId3);

        Order_ThreewayCall.ThreewayCall(orderId4,"success");
        status = Order_Rollback.Rollback(orderId4);
        if(!status.equals("2000")) {
            logger.debug(status);
            logger.error("回退订单失败"+orderId4);
            Assert.fail("回退订单失败"+orderId4);
        }
        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId4);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId3);
    }

    @Test
    public void test_03_我收到的手术单_接收时间排序() {
        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = CreateVerifiedDoctor(dp);
        String tmpToken = result.get("token");
        String tmpExpertId = result.get("expert_id");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","2"); //上级医生，我收到的订单
        query.put("sortCriteria","1");// 接收时间倒序
        query.put("collatingSequence","0");

        logger.info("创建订单mainToken");
        String orderId1 = CreateOrder.CreateOrder(mainToken);
        String orderId2 = CreateOrder.CreateOrder(mainToken);
        String orderId3 = CreateOrder.CreateOrder(mainToken);
        String orderId4 = CreateOrder.CreateOrder(mainToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            Assert.fail("创建订单with mainToken失败");
        }

        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "0");

        logger.info("依次领取和推荐专家");
        Order_ReceiveTask.receiveTask(orderId1);
        Order_ReceiveTask.receiveTask(orderId2);
        Order_ReceiveTask.receiveTask(orderId3);
        Order_ReceiveTask.receiveTask(orderId4);
        String status = Order_RecommendDoctor.recommendDoctor(orderId4, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try  {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        status = Order_RecommendDoctor.recommendDoctor(orderId3, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try  {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        status = Order_RecommendDoctor.recommendDoctor(orderId2, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try  {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        status = Order_RecommendDoctor.recommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }

        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);

        status = Order_Rollback.Rollback(orderId3);
        if(!status.equals("2000")) {
            logger.error("回退订单失败"+orderId3);
            Assert.fail("回退订单失败"+orderId3);
        }
        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);
// 后面没法做，无法支付，需要支付后再取消，这样不清空上级专家信息，
        status = Order_Reject.rejectOrder(orderId4);
        if(!status.equals("9000")) {
            logger.error("拒绝订单失败"+orderId4);
            Assert.fail("拒绝订单失败"+orderId4);
        }
        try {
            res = HttpRequest.sendGet(host_doc + uri, query, tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(UT.parseJson(data, "order()"), "4");
        Assert.assertEquals(UT.parseJson(data, "order(0):order_number"), orderId1);
        Assert.assertEquals(UT.parseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(UT.parseJson(data, "order(2):order_number"), orderId3);
        Assert.assertEquals(UT.parseJson(data, "order(3):order_number"), orderId4);
    }

    public void test_04_验证我发起的手术单_状态筛选() {

    }

}
