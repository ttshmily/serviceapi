package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.testcase.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.testcase.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.mingyizhudao.qa.util.UT.parseJson;

/**
 * Created by ttshmily on 20/3/2017.
 */
public class GetDoctorProfile extends BaseTest {

    public static final Logger logger= Logger.getLogger(GetDoctorProfile.class);
    public static String uri = "/api/getdoctorprofile";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";


    public static String getDoctorProfile(String token) {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri, "", token);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");

    }

    @Test
    public void test_02_没有token信息的请求不能获得个人信息并返回正确的错误提示() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
    //    logger.info(unicodeString(res));
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000","没有登录信息，不应该返回data");

    }

    @Test
    public void test_03_错误token的请求不能获得个人信息并返回正确的错误提示() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", "nidawoya");
        } catch (IOException e) {
            logger.error(e);
        }
        //    logger.info(unicodeString(res));
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000","错误token信息，不应该返回data");

    }

    @Test
    public void test_04_测试data字段返回了足够的医生信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_required"), "is_required字段缺失");
    }

    //created by tianjing on 2017/6/27
    @Test
    public void test_05_测试总订单数_测试处理中订单数_CRM未领取(){

        String resOld = "";
        try {
            resOld = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));

        int i = 1;
        while (i<3){
            CreateOrder.CreateOrder(mainToken);
            i++;
        }
        //CreateOrder.CreateOrder(mainToken);
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling+i-1),"处理中的订单数不正确");
    }

    @Test
    public void test_06_测试总订单数_测试处理中订单数_CRM已领取(){
        String resOld = "";
        try {
            resOld = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));

        int i = 1;
        while (i<4){
            String orderId = CreateOrder.CreateOrder(mainToken);
            Order_ReceiveTask.receiveTask(orderId);
            i++;
        }
        //CreateOrder.CreateOrder(mainToken);
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling+i-1),"处理中的订单数不正确");
    }

    @Test
    public void test_07_测试总订单数_测试处理中订单数_推荐完医生(){
        String resOld = "";
        try {
            resOld = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));

        int i = 1;
        while (i<3){
            String orderId = CreateOrder.CreateOrder(mainToken);
            Order_ReceiveTask.receiveTask(orderId);
            Order_RecommendDoctor.recommendDoctor(orderId,"3721");
            i++;
        }
        //CreateOrder.CreateOrder(mainToken);
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling+i-1),"处理中的订单数不正确");
    }

    @Test
    public void test_08_测试总订单数_待支付状态的订单数(){
        String resOld = "";
        try {
            resOld = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));
        int pendingpayment = Integer.parseInt(parseJson(data, "doctor:status_count:agentId:pendingpayment"));

        int  i= 1;
        while (i<4){
            String orderId = CreateOrder.CreateOrder(mainToken);
            Order_ReceiveTask.receiveTask(orderId);
            Order_RecommendDoctor.recommendDoctor(orderId,"3721");
            Order_ThreewayCall.ThreewayCall(orderId,"success");
            i++;
        }
        String res = "";
        try {
            res = HttpRequest.sendGet(host_doc +mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling),"处理中的订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:pendingpayment"), String.valueOf(pendingpayment+i-1),"待支付的订单数不正确");
    }

    @Test
    public void test_09_测试返回的地区服务专员(){
        //TODO
    }

}
