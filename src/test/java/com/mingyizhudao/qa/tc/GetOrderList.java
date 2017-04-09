package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderList extends BaseTest{
    // TODO

    public static String uri = "/api/getorderlist";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void 获取订单列表_登录用户() {
        String res = "";
        CreateOrder.CreateOrder(mainToken);
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(parseJson(data,"order():patient_name"), "", "患者姓名字段缺失");
        Assert.assertNotEquals(parseJson(data,"order():patient_gender"), "", "患者性别字段缺失");
        Assert.assertNotEquals(parseJson(data,"order():patient_phone"), "", "患者手机号字段缺失");
        Assert.assertNotNull(parseJson(data,"order():major_disease_id"), "主诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order():major_disease_name"), "", "主诉疾病名称字段缺失");
        Assert.assertNotNull(parseJson(data,"order():patient_gender_text"), "次诉疾病ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order():order_number"), "", "订单ID字段缺失");
        Assert.assertNotEquals(parseJson(data,"order():status"), "", "订单状态字段缺失");
        Assert.assertNotNull(parseJson(data,"order():OrderStatusText"), "订单状态描述字段缺失");
        Assert.assertNotNull(parseJson(data,"order():surgeon_id"), "手术医生ID字段不能缺失");
        Assert.assertNotNull(parseJson(data,"order():surgeon_name"), "手术医生姓名字段不能缺失");
        Assert.assertNotEquals(parseJson(data,"order():surgeon_hospital"), "", "手术医生所在医院字段不能缺失");

    }

    @Test
    public void 获取订单列表_登录用户_订单列表会更新且按时间倒序() {
        String res = "";
        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();
        res = GetDoctorProfile.getDoctorProfile(tmpToken);
        CrmCertifiedDoctor.certify(parseJson(JSONObject.fromObject(res), "data:doctor:user_id"));

        String orderId1 = CreateOrder.CreateOrder(tmpToken);
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId1);

        String orderId2 = CreateOrder.CreateOrder(tmpToken);
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId2);
        Assert.assertEquals(parseJson(data, "order(1):id"), orderId1);

        String orderId3 = CreateOrder.CreateOrder(tmpToken);
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId3);
        Assert.assertEquals(parseJson(data, "order(1):id"), orderId2);
        Assert.assertEquals(parseJson(data, "order(2):id"), orderId1);
    }

    @Test
    public void 获取订单列表_未登录用户() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        // TODO
        Assert.assertEquals(code, "2210304");
    }


}
