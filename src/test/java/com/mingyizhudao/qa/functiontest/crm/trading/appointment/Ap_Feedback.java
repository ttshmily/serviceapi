package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_AddAccount.s_AddPayAccount;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Confirm.s_Confirm;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_Create.s_Create;
import static com.mingyizhudao.qa.functiontest.crm.trading.appointment.Ap_RiskControl.s_Take;
import static com.mingyizhudao.qa.utilities.Generator.randomDateFromNow;
import static com.mingyizhudao.qa.utilities.Generator.randomInt;
import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;
import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPut;

public class Ap_Feedback extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/feedback";

    @Test
    public void test_01_创建回访记录() {
        String res = "";
        Random random = new Random();
        String[] type = new String[] {"WECHAT", "PHONE", "FACE_TO_FACE"};
        String orderNumber = s_Create(new AppointmentOrder());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        if(!s_Take(orderNumber) || !s_Confirm(orderNumber) || !s_AddPayAccount(orderNumber)) {
            Assert.fail("待支付订单生成失败 or 暂无已支付订单");
        }
        JSONObject body = new JSONObject();
        body.put("return_visit_date", randomDateFromNow(1,2, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("return_visit_remark", "我爱名医主刀");
        body.put("return_visit_satisfaction", randomInt(3));
        body.put("return_visit_type", type[random.nextInt(type.length)]);  //WECHAT-微信 PHONE-电话 FACE_TO_FACE-面基

        res = s_SendPut(host_crm + uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "major_recommended_doctor_id"), body.getString("major_recommended_doctor_id"));
    }
}
