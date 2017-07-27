package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by TianJing on 2017/6/26.
 */
public class OrderDetail extends BaseTest{
    public static final Logger logger= Logger.getLogger(OrderDetail.class);
    public static String uri = "/api/v1/orders/orderDetail";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    @Test
    public void test_01_没有token或token错误无权限使用接口() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, "",null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");

        try {
            res = HttpRequest.sendGet(host_bda + uri, query, "aaa",null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "token错误不应该调用成功");
    }

    @Test
    public void test_02_登录用户_不传入订单编号获取订单详情(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, bda_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1411002", "订单编号不能为空");

    }

    @Test
    public void test_03_登录用户_传入订单编号获取订单详情_不考虑员工订单不能相互查看订单的情况(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("orderNumber", "1017690178");
        try {
            res = HttpRequest.sendGet(host_bda + uri, query, bda_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        //String orderDetail = data.getString("list");
        Assert.assertNotNull(Generator.parseJson(data, "order_number"), "order_number字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "status"), "status字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "agent_id"), "agent_id段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "agent_name"), "agent_name字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "surgeon_id"), "surgeon_id字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "surgeon_name"), "surgeon_name字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "deposit_payment_created_at"), "deposit_payment_created_at字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "deposit_payment_paid_at"), "deposit_payment_paid_at字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "patient_name"), "patient_name字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "major_disease_id"), "major_disease_id字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "major_disease_name"), "major_disease_name字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "expected_surgery_start_date"), "expected_surgery_start_date字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "expected_surgery_due_date"), "expected_surgery_due_date字段缺失");
        Assert.assertNotNull(Generator.parseJson(data, "expected_surgery_hospital_id"), "expected_surgery_hospital_id字段缺失");
    }

        @Test
        public void test_04_登录用户_传入不存在的订单编号获取订单详情(){
            String res = "";
            HashMap<String, String> query = new HashMap<>();
            query.put("orderNumber", "0000000000");
            try {
                res = HttpRequest.sendGet(host_bda + uri, query, bda_token, null);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertNotEquals(code, "1000000", "传入不存在的订单编号应返回订单号不存在");
        }
}
