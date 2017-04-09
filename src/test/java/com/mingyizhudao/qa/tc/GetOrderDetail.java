package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderDetail extends BaseTest{
    // TODO

    public static String uri = "/api/getorderdetail/{orderId}";
    public static String mock = false ? "/mockjs/1" : "";


    public static String getOrderDetail(String token, String orderId) {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.sendGet(host+mock+uri, "", token, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void 获取订单详情_提供正确的属于自己的订单ID() {
        String res = "";

        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", CreateOrder.CreateOrder(mainToken));
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data,"order:id"), "", "订单ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(parseJson(data,"order:major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"order:minor_disease_id"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:minor_disease_name"), "", "次诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"order:diagnosis"), "病例描述字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_start_date"), "期望手术最早开始时间字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_due_date"), "期望手术最晚开始时间字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_hospital_id"), "期望医院ID字段缺失");
        Assert.assertNotNull(parseJson(data,"order:expected_surgery_hospital_name"), "期望医院名称字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:status"), "", "订单状态字段缺失");
        Assert.assertNotNull(parseJson(data,"order:OrderStatusText"), "订单状态描述字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:created_at"), "", "订单创建时间字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:order_number"), "", "订单号时间字段缺失");
        Assert.assertNotEquals(parseJson(data,"order:pics"), "", "订单号时间字段缺失");

    }

    @Test
    public void 获取订单详情_提供正确的错误的ID_ID为非法整数() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000000000");
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNull(data, "order:id");

    }

    @Test
    public void 获取订单详情_提供正确的错误的ID_ID为英文() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        pathValue.put("orderId", "20000asdfa000");
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", mainToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void 获取订单详情_提供不属于自己的订单ID() {
        String res = "";
        HashMap<String, String> pathValue = new HashMap<String, String>();
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
        }
        pathValue.put("orderId", orderId);
        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();
        HashMap<String, String> profile = new HashMap<String, String>();
        profile.put("hospital_id","4");
        profile.put("name", "temp-test");
        UpdateDoctorProfile.updateDoctorProfile(tmpToken, profile);
        res = GetDoctorProfile.getDoctorProfile(tmpToken);
        CrmCertifiedDoctor.certify(parseJson(JSONObject.fromObject(res), "data:doctor:user_id"));
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken, pathValue);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }


}
