package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_AddAccount.s_AddPayAccount;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;

public class Ap_UpdateOrderFee extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/updateOrderFee";

    @Test
    public void test_01_更新预约单的成本价和盈利额() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
//        String payId = Ap_CreatePayment_V2.s_CreatePayment(orderNumber, 1);
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        int platform_fee = Integer.parseInt(data.getString("platform_fee"));
        int doctor_fee = Integer.parseInt(data.getString("doctor_fee"));
        int total = platform_fee + doctor_fee;
        logger.info("订单原总金额是："+total);
        logger.info("修改减小总金额：");
        JSONObject body=new JSONObject();
        platform_fee = (int)Generator.randomInt(total/2);
        doctor_fee = (int)Generator.randomInt(total/2);
        body.put("doctor_fee", doctor_fee);
        body.put("platform_fee", platform_fee);
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("doctor_fee"), String.valueOf(doctor_fee));
        Assert.assertEquals(data.getString("platform_fee"), String.valueOf(platform_fee));

        logger.info("修改增大总金额：");
        platform_fee = (int)Generator.randomInt(total/2)+total;
        doctor_fee = (int)Generator.randomInt(total/2);
        body.put("doctor_fee", doctor_fee);
        body.put("platform_fee", platform_fee);
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("doctor_fee"), String.valueOf(doctor_fee));
        Assert.assertEquals(data.getString("platform_fee"), String.valueOf(platform_fee));
    }

    @Test
    public void test_02_创建过支付链接的订单修改订单总金额() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        String payId = Ap_CreatePayment_V2.s_CreatePayment(orderNumber, 1);
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        int platform_fee = Integer.parseInt(data.getString("platform_fee"));
        int doctor_fee = Integer.parseInt(data.getString("doctor_fee"));
        int total = platform_fee + doctor_fee;
        logger.info("订单原总金额是：" + total);
        logger.info("修改增加总金额：");
        JSONObject body=new JSONObject();
        platform_fee = (int)Generator.randomInt(total/2)+total;
        doctor_fee = (int)Generator.randomInt(total/2);
        body.put("doctor_fee", doctor_fee);
        body.put("platform_fee", platform_fee);
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("doctor_fee"), String.valueOf(doctor_fee));
        Assert.assertEquals(data.getString("platform_fee"), String.valueOf(platform_fee));

        logger.info("修改减小总金额：");
        platform_fee = 1;
        doctor_fee = (int)Generator.randomInt(total/2);
        body.put("doctor_fee", doctor_fee);
        body.put("platform_fee", platform_fee);
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("doctor_fee"), String.valueOf(doctor_fee));
        Assert.assertEquals(data.getString("platform_fee"), String.valueOf(platform_fee));
    }

    @Test
    public void test_03_创建过支付链接的订单修改订单总金额_修改金额不能低于支付链接金额() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        int platform_fee = Integer.parseInt(data.getString("platform_fee"));
        int doctor_fee = Integer.parseInt(data.getString("doctor_fee"));
        int total = platform_fee + doctor_fee;
        int paid = (int)Generator.randomInt(total);
        String payId = Ap_CreatePayment_V2.s_CreatePayment(orderNumber, paid);
        logger.info("订单原总金额是：" + total);
        logger.info("修改减小总金额：");
        JSONObject body=new JSONObject();
        platform_fee = (int)Generator.randomInt(paid/2);
        doctor_fee = (int)Generator.randomInt(paid/2);
        body.put("doctor_fee", doctor_fee);
        body.put("platform_fee", platform_fee);
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(Integer.parseInt(data.getString("doctor_fee")) + Integer.parseInt(data.getString("platform_fee")), total);
    }

    @Test
    public void test_04_创建过支付链接的订单修改订单总金额_支付链接金额禁用() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        int platform_fee = Integer.parseInt(data.getString("platform_fee"));
        int doctor_fee = Integer.parseInt(data.getString("doctor_fee"));
        int total = platform_fee + doctor_fee;
        int paid = (int)Generator.randomInt(total);
        String payId = Ap_CreatePayment_V2.s_CreatePayment(orderNumber, paid);
        Ap_DisablePayLink.s_DisablePayment(payId);
        logger.info("订单原总金额是：" + total);
        logger.info("修改减小总金额：");
        JSONObject body=new JSONObject();
        platform_fee = (int)Generator.randomInt(paid/2);
        doctor_fee = (int)Generator.randomInt(paid/2);
        body.put("doctor_fee", doctor_fee);
        body.put("platform_fee", platform_fee);
        pathValue.put("orderNumber", orderNumber);
        res = HttpRequest.s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("doctor_fee"), String.valueOf(doctor_fee));
        Assert.assertEquals(data.getString("platform_fee"), String.valueOf(platform_fee));    }
}
