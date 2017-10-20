package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_AddAccount.s_AddPayAccount;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;

public class Ap_DisablePayLink extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{paymentNumber}/disable";

    public static boolean s_DisablePayment(String paymentNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("paymentNumber", paymentNumber);//APPOINTMENT-全款 DOCTOR-成本价 PLATFORM-尾款
        String res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        return JSONObject.fromObject(res).getJSONObject("data").getString("enabled").equals("false");
    }

    @Test
    public void test_01_禁用支付链接() {
        String res = "";
        AppointmentOrder ap = new AppointmentOrder();
        String orderNumber = s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        if (!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("订单未添加医生收款账号信息");
        }
        String payId = Ap_CreatePayment_V2.s_CreatePayment(orderNumber, 1);
        pathValue.put("paymentNumber", payId);
        res = HttpRequest.s_SendPost(host_crm + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getString("payment_number"), payId);
        Assert.assertEquals(data.getString("enabled"), "false");
    }

}
