package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.functiontest.doctor.CreateOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by TianJing on 2017/6/26.
 */
public class OrderDetail extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}";
    
    @Test
    public void test_01_没有token或token错误无权限使用接口() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");

        res = HttpRequest.s_SendGet(host_bda + uri, query, "aaa");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "token错误不应该调用成功");
    }

    @Test
    public void test_02_登录用户_不传入订单编号获取订单详情(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "订单编号不能为空");

    }

    @Test
    public void test_03_登录用户_传入订单编号获取订单详情_不考虑员工订单不能相互查看订单的情况(){
        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        String order_number = CreateOrder.s_CreateOrder(s_CreateSyncedDoctor(new User()).get("token"));
        Order_ReceiveTask.s_ReceiveTask(order_number);
        Order_RecommendDoctor.s_RecommendDoctor(order_number, "666");
        Order_ThreewayCall_V2.s_CallV2(order_number, "success");
        pathValue.put("orderNumber", order_number);
        res = HttpRequest.s_SendGet(host_bda + uri, "", bda_session, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        //String orderDetail = data.getString("list");
        Assert.assertNotNull(Helper.s_ParseJson(data, "order_number"), "order_number字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "status"), "status字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "agent_id"), "agent_id段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "agent_name"), "agent_name字段缺失");
//        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_id"), "surgeon_id字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeon_name"), "surgeon_name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "deposit_payment_created_at"), "deposit_payment_created_at字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "deposit_payment_paid_at"), "deposit_payment_paid_at字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "patient_name"), "patient_name字段缺失");
//        Assert.assertNotNull(Helper.s_ParseJson(data, "major_disease_id"), "major_disease_id字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "major_disease_name"), "major_disease_name字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "expected_surgery_start_date"), "expected_surgery_start_date字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "expected_surgery_due_date"), "expected_surgery_due_date字段缺失");
        Assert.assertNotNull(Helper.s_ParseJson(data, "expected_surgery_hospital_id"), "expected_surgery_hospital_id字段缺失");
    }

        @Test
        public void test_04_登录用户_传入不存在的订单编号获取订单详情(){
            String res = "";
            HashMap<String, String> pathValue = new HashMap<>();
            pathValue.put("orderNumber", "0000000000");
            res = HttpRequest.s_SendGet(host_bda + uri, "", bda_session, pathValue);
            s_CheckResponse(res);
            Assert.assertNull(data, "传入不存在的订单编号应返回订单号不存在");
        }
}
