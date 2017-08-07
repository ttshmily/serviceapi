package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.*;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by dayi on 2017/6/27.
 */
public class GetOrderList_V1 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/orders";

    public static String s_List(String token, String flag) {
        // 1 - 下级医生；2 - 上级医生
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> query = new HashMap<>();
        query.put("flag", flag);
        res = HttpRequest.s_SendGet(host_doc+uri, query, token);
        JSONObject orderList = JSONObject.fromObject(res).getJSONObject("data");
        return String.valueOf(orderList.getJSONArray("order").size());
    }

    public static String s_ListCompleted(String token, String flag) {
        // 1 - 下级医生；2 - 上级医生
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> query = new HashMap<>();
        query.put("flag",flag);
        query.put("status", "9000");
        query.put("page", "1");
        query.put("pageSize", "1");
        res = HttpRequest.s_SendGet(host_doc+uri, query, token);
        if (Helper.s_ParseJson(JSONObject.fromObject(res), "data:size") == "0") return null;
        return Helper.s_ParseJson(JSONObject.fromObject(res), "data:order(0):order_number");
    }

    @Test
    public void test_01_我发起的手术单_默认排序() {//已取消置底，已完成倒数第二顺位，其它会诊单按创建时间倒序混排
        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = s_CreateSyncedDoctor(dp);
        String tmpToken = result.get("token");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","1"); //下级医生

        logger.info("创建订单with tmpToken");
        String orderId1 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId2 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId2.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc +uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "2");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId3 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId3.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "3");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId4 = CreateOrder.s_CreateOrder(tmpToken);
        if (orderId4.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            Assert.fail("创建订单with tmpToken失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId1);

        logger.info("子用例1：拒绝一个订单：期望其置底");
        Order_ReceiveTask.s_ReceiveTask(orderId4);
        String status = Order_Reject.s_RejectOrder(orderId4);
        if(!status.equals("9000")) {
            Assert.fail("拒绝订单失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId4);

        logger.info("子用例2：领取并推荐一个订单：处理中，位置不变");
        Order_ReceiveTask.s_ReceiveTask(orderId2);
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId2, mainExpertId);
        if(!status.equals("2020")) {
            Assert.fail("推荐专家失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId4);

        logger.info("子用例3：领取推荐并成功创建三方通话一个订单，处理中，位置不变");
        Order_ReceiveTask.s_ReceiveTask(orderId1);
        Order_RecommendDoctor.s_RecommendDoctor(orderId1, mainExpertId);
        status = Order_ThreewayCall_V2.s_CallV2(orderId1, "success");
        if(!status.equals("3000")) {
            Assert.fail("三方通话调用失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId4);
    }

    @Test
    public void test_02_我收到的手术单_默认排序() {//处理中->已完成->已取消，同一状态按接收时间倒序

        String res = "";
        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = s_CreateSyncedDoctor(dp);
        String tmpToken = result.get("token");
        String tmpExpertId = result.get("expert_id");

        logger.info("创建4条测试订单");
        String orderId1 = CreateOrder.s_CreateOrder(mainToken);
        String orderId2 = CreateOrder.s_CreateOrder(mainToken);
        String orderId3 = CreateOrder.s_CreateOrder(mainToken);
        String orderId4 = CreateOrder.s_CreateOrder(mainToken);

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","2"); //上级医生

        if (orderId1.isEmpty()||orderId2.isEmpty()||orderId3.isEmpty()||orderId4.isEmpty()) {
            Assert.fail("创建订单测试订单失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "0");
        logger.info("测试医生收到"+ Helper.s_ParseJson(data, "order()")+"条订单");

        logger.info("依次领取和推荐测试医生作为专家");
        Order_ReceiveTask.s_ReceiveTask(orderId1);
        Order_ReceiveTask.s_ReceiveTask(orderId2);
        Order_ReceiveTask.s_ReceiveTask(orderId3);
        Order_ReceiveTask.s_ReceiveTask(orderId4);
        String status = Order_RecommendDoctor.s_RecommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId2, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId3, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId4, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId1);
        logger.info("测试医生收到"+ Helper.s_ParseJson(data, "order()")+"条订单");

        Order_ThreewayCall_V2.s_CallV2(orderId3,"success");
        status = Order_Rollback.s_Rollback(orderId3);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId3);
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId1);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId3);

        Order_ThreewayCall_V2.s_CallV2(orderId4,"success");
        status = Order_Rollback.s_Rollback(orderId4);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId4);
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId1);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId3);

        status = Order_ThreewayCall_V2.s_CallV2(orderId2,"failed");
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("三方通话调用失败"+orderId2);
        }
        Order_RecommendDoctor.s_RecommendDoctor(orderId2, Generator.randomExpertId());
        status = Order_ThreewayCall_V2.s_CallV2(orderId2,"success");
        if(!status.equals("3000")) {
            logger.debug(status);
            Assert.fail("三方通话调用失败"+orderId2);
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId1);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId4); //取消
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId3); //取消
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId2); //取消
    }

    @Test
    public void test_03_我收到的手术单_接收时间排序() {
        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = s_CreateSyncedDoctor(dp);
        String tmpToken = result.get("token");
        String tmpExpertId = result.get("expert_id");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","2"); //上级医生，我收到的订单
        query.put("sortCriteria","1");// 接收时间倒序
        query.put("collatingSequence","0");

        logger.info("创建4条测试订单");
        String orderId1 = CreateOrder.s_CreateOrder(mainToken);
        String orderId2 = CreateOrder.s_CreateOrder(mainToken);
        String orderId3 = CreateOrder.s_CreateOrder(mainToken);
        String orderId4 = CreateOrder.s_CreateOrder(mainToken);

        if (orderId1.isEmpty()||orderId2.isEmpty()||orderId3.isEmpty()||orderId4.isEmpty()) {
            Assert.fail("创建订单测试订单失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "0");
        logger.info("测试医生收到"+ Helper.s_ParseJson(data, "order()")+"条订单");

        logger.info("依次领取和推荐测试医生作为专家");
        Order_ReceiveTask.s_ReceiveTask(orderId1);
        Order_ReceiveTask.s_ReceiveTask(orderId2);
        Order_ReceiveTask.s_ReceiveTask(orderId3);
        Order_ReceiveTask.s_ReceiveTask(orderId4);
        String status = Order_RecommendDoctor.s_RecommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId2, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId3, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId4, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("推荐专家失败");
            Assert.fail("推荐专家失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId1);
        logger.info("测试医生收到"+ Helper.s_ParseJson(data, "order()")+"条订单");

        Order_ThreewayCall_V2.s_CallV2(orderId3,"success");
        status = Order_Rollback.s_Rollback(orderId3);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId3);
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId1);

        Order_ThreewayCall_V2.s_CallV2(orderId4,"success");
        status = Order_Rollback.s_Rollback(orderId4);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId4);
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "4");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(2):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(3):order_number"), orderId1);
    }

    @Test
    public void test_04_验证我收到的手术单_状态筛选() {
        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = s_CreateSyncedDoctor(dp);
        String tmpToken = result.get("token");
        String tmpExpertId = result.get("expert_id");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","2"); //上级医生，我收到的订单
        query.put("sortCriteria","1");// 接收时间倒序
        query.put("collatingSequence","0");

        logger.info("创建4条测试订单");
        String orderId1 = CreateOrder.s_CreateOrder(mainToken);
        String orderId2 = CreateOrder.s_CreateOrder(mainToken);
        String orderId3 = CreateOrder.s_CreateOrder(mainToken);
        String orderId4 = CreateOrder.s_CreateOrder(mainToken);

        if (orderId1.isEmpty()||orderId2.isEmpty()||orderId3.isEmpty()||orderId4.isEmpty()) {
            Assert.fail("创建订单测试订单失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "0");
        logger.info("测试医生收到"+ Helper.s_ParseJson(data, "order()")+"条订单");

        logger.info("依次领取和推荐测试医生作为专家");
        Order_ReceiveTask.s_ReceiveTask(orderId1);
        Order_ReceiveTask.s_ReceiveTask(orderId2);
        Order_ReceiveTask.s_ReceiveTask(orderId3);
        Order_ReceiveTask.s_ReceiveTask(orderId4);
        String status = Order_RecommendDoctor.s_RecommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId2, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId3, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId4, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }

        Order_ThreewayCall_V2.s_CallV2(orderId3,"success");
        status = Order_Rollback.s_Rollback(orderId3);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId3);
        }
        Order_ThreewayCall_V2.s_CallV2(orderId4,"success");
        status = Order_Rollback.s_Rollback(orderId4);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId4);
        }

        logger.info("筛选已取消状态（4030,9000）");
        query.put("status","4030,9000");
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "2");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId4);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId3);

        logger.info("筛选处理中状态（2020,3000,4000,4010）");
        query.replace("status","2020,3000,4000,4010");
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "2");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId2);
        Assert.assertEquals(Helper.s_ParseJson(data, "order(1):order_number"), orderId1);

        logger.info("筛选已完成状态（4020, 5000）");
        query.replace("status","4020, 5000");
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
//        Assert.assertEquals(UT.s_ParseJson(data, "order()"), "2");
//        Assert.assertEquals(UT.s_ParseJson(data, "order(0):order_number"), orderId4);
//        Assert.assertEquals(UT.s_ParseJson(data, "order(1):order_number"), orderId3);
    }

    @Test
    public void test_05_验证我收到的手术单_重新选择同一个上级专家() {
        String res = "";

        DoctorProfile dp = new DoctorProfile(true);
        HashMap<String, String> result = s_CreateSyncedDoctor(dp);
        String tmpToken = result.get("token");
        String tmpExpertId = result.get("expert_id");

        HashMap<String, String> query = new HashMap<>();
        query.put("flag","2"); //上级医生，我收到的订单
        query.put("sortCriteria","1");// 接收时间倒序
        query.put("collatingSequence","0");

        logger.info("创建4条测试订单");
        String orderId1 = CreateOrder.s_CreateOrder(mainToken);
        String orderId2 = CreateOrder.s_CreateOrder(mainToken);

        if (orderId1.isEmpty()||orderId2.isEmpty()) {
            Assert.fail("创建订单测试订单失败");
        }
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "0");
        logger.info("测试医生收到"+ Helper.s_ParseJson(data, "order()")+"条订单");

        logger.info("依次领取和推荐测试医生作为专家");
        Order_ReceiveTask.s_ReceiveTask(orderId1);
        Order_ReceiveTask.s_ReceiveTask(orderId2);
        String status = Order_RecommendDoctor.s_RecommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId2, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }

        Order_ThreewayCall_V2.s_CallV2(orderId1,"success");
        status = Order_Rollback.s_Rollback(orderId1);
        if(!status.equals("2000")) {
            logger.debug(status);
            Assert.fail("回退订单调用失败"+orderId1);
        }

        logger.info("筛选已取消状态（4030,9000）");
        query.put("status","4030,9000");
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "1");
        Assert.assertEquals(Helper.s_ParseJson(data, "order(0):order_number"), orderId1);

        logger.info("重新推荐相同的医生");
        status = Order_RecommendDoctor.s_RecommendDoctor(orderId1, tmpExpertId);
        if(!status.equals("2020")) {
            logger.error("status = " + status);
            Assert.fail("推荐专家失败");
        }

        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "0", "订单order1应当不在已取消状态");

        logger.info("筛选已取消状态（2020,3000,4000,4010）");
        query.replace("status","2020,3000,4000,4010");
        res = HttpRequest.s_SendGet(host_doc + uri, query, tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "order()"), "2","订单order1应当恢复到处理中");
    }
}
