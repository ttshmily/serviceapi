package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.CrmCertifiedDoctor;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.testng.Assert.fail;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class GetOrderList extends BaseTest{

    public static final Logger logger= Logger.getLogger(GetOrderList.class);
    public static String uri = "/api/getorderlist";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void 获取订单列表_登录用户() {
        String res = "";
        logger.info("创建订单with mainToken");
        String orderId = CreateOrder.CreateOrder(mainToken);
        if (orderId.isEmpty()) {
            logger.error("创建订单with mainToken失败");
            fail();
        }
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
        Assert.assertNotEquals(parseJson(data,"order():OrderStatusText"), "", "订单状态描述字段缺失");
        Assert.assertNotNull(parseJson(data,"order():surgeon_id"), "手术医生ID字段不能缺失");
        Assert.assertNotNull(parseJson(data,"order():surgeon_name"), "手术医生姓名字段不能缺失");
        Assert.assertNotNull(parseJson(data,"order():surgeon_hospital"), "手术医生所在医院字段不能缺失");

    }

    @Test
    public void 获取订单列表_登录用户_订单列表会更新且按时间倒序() {
        String res = "";
        SendVerifyCode.send();
        String tmpToken = CheckVerifyCode.check();
        res = GetDoctorProfile.getDoctorProfile(tmpToken);
        HashMap<String, String> profile = new HashMap<String, String>();
        profile.put("hospital_id","4");
        profile.put("name", "temp-test");
        UpdateDoctorProfile.updateDoctorProfile(tmpToken, profile);
        CrmCertifiedDoctor.certify(parseJson(JSONObject.fromObject(res), "data:doctor:user_id"));

        logger.info("创建订单with tmpToken");
        String orderId1 = CreateOrder.CreateOrder(tmpToken);
        if (orderId1.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            fail();
        }
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId2 = CreateOrder.CreateOrder(tmpToken);
        if (orderId2.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            fail();
        }
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId2);
        Assert.assertEquals(parseJson(data, "order(1):id"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId3 = CreateOrder.CreateOrder(tmpToken);
        if (orderId3.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            fail();
        }
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId3);
        Assert.assertEquals(parseJson(data, "order(1):id"), orderId2);
        Assert.assertEquals(parseJson(data, "order(2):id"), orderId1);

        logger.info("创建订单with tmpToken");
        String orderId4 = CreateOrder.CreateOrder(tmpToken);
        if (orderId4.isEmpty()) {
            logger.error("创建订单with tmpToken失败");
            fail();
        }
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", tmpToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(parseJson(data, "order(0):id"), orderId4);
        Assert.assertEquals(parseJson(data, "order(1):id"), orderId3);
        Assert.assertEquals(parseJson(data, "order(2):id"), orderId2);
        Assert.assertEquals(parseJson(data, "order(3):id"), orderId1);
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
        Assert.assertEquals(code, "2210304", "应当提示未登录");
    }


}
