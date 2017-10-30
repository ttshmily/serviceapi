package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Random;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Complete extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/complete";

    public static boolean s_Complete(AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        String res = HttpRequest.s_SendPut(host_ims+uri, at.transform(), crm_token);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        //TODO
        return r.getJSONObject("data").getString("status").equals("COMPLETED") &&
                r.getJSONObject("data").getString("status").equals("5000");
    }

    @Test
    public void test_01_完成订单_受理中() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid  =Create.s_CreateTid(at);
        String orderNumber = getOrderNumberByTid(tid);
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("actual_appointment_date", Generator.randomDateTillNow(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        Random random = new Random();
        String[] change_reasons = new String[]{"DOCTOR_TIME_CHANGE", "PATIENT_TIME_CHANGE", "CANT_REGISTER", "FORCE_MAJEURE"};
        String[] visit_types = new String[] {"WECHAT", "PHONE", "FACE_TO_FACE"};
        body.put("date_change_reason", change_reasons[random.nextInt(change_reasons.length)]);
        body.put("return_visit_date", Generator.randomDateFromNow(0, 0, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("return_visit_remark", "自动化");
        body.put("return_visit_satisfaction", Generator.randomInt(4));
        body.put("return_visit_type", visit_types[random.nextInt(visit_types.length)]);

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Detail.s_Detail(tid);
        s_CheckResponse(res);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "COMPLETE");
    }

    @Test
    public void test_02_完成订单_服务中() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        HashMap<String, String> pathValue = new HashMap<>();
        String tid = Create.s_CreateTid(at);
        String orderNumber = getOrderNumberByTid(tid);
        ConfirmExpert.s_ConfirmExpert(orderNumber);
        CreatePayLink.s_CreatePayment(orderNumber, 1);
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("actual_appointment_date", Generator.randomDateTillNow(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        Random random = new Random();
        String[] change_reasons = new String[]{"DOCTOR_TIME_CHANGE", "PATIENT_TIME_CHANGE", "CANT_REGISTER", "FORCE_MAJEURE"};
        String[] visit_types = new String[] {"WECHAT", "PHONE", "FACE_TO_FACE"};
        body.put("date_change_reason", change_reasons[random.nextInt(change_reasons.length)]);
        body.put("return_visit_date", Generator.randomDateFromNow(0, 0, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        body.put("return_visit_remark", "自动化");
        body.put("return_visit_satisfaction", Generator.randomInt(4));
        body.put("return_visit_type", visit_types[random.nextInt(visit_types.length)]);

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Detail.s_Detail(tid);
        s_CheckResponse(res);

        Assert.assertEquals(data.getString("status"), "COMPLETE");
        Assert.assertEquals(data.getJSONObject("appointment_order").getString("appointment_status"), "COMPLETE");
    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }
}
