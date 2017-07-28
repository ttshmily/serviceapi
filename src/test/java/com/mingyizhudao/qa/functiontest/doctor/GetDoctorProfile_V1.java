package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.Order_ThreewayCall;
import com.mingyizhudao.qa.functiontest.crm.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.mingyizhudao.qa.utilities.Generator.parseJson;

/**
 * Created by ttshmily on 20/3/2017.
 */
public class GetDoctorProfile_V1 extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/getdoctorprofile";

    public static String s_MyProfile(String token) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        try {
            res = HttpRequest.s_SendGet(host_doc +uri, "", token);
        } catch (IOException e) {
            logger.error(e);
        }
        return res;
    }

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
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
            res = HttpRequest.s_SendGet(host_doc+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
    //    logger.info(unicodeString(res));
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000","没有登录信息，不应该返回data");

    }

    @Test
    public void test_03_错误token的请求不能获得个人信息并返回正确的错误提示() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", "nidawoya");
        } catch (IOException e) {
            logger.error(e);
        }
        //    logger.info(unicodeString(res));
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000","错误token信息，不应该返回data");

    }

    @Test
    public void test_04_测试data字段返回了足够的医生信息() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
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

        CreateOrder.s_CreateOrder(mainToken);
        String resOld = "";
        try {
            resOld = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(resOld);
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));
        System.out.println("总订单数：" + count);
        System.out.println("处理中订单数：" + countHandling);

        int i = 1;
        while (i<3){
            CreateOrder.s_CreateOrder(mainToken);
            i++;
        }
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
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
            resOld = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(resOld);
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));

        int i = 1;
        while (i<2){
            String orderId = CreateOrder.s_CreateOrder(mainToken);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            i++;
        }
        //s_CreateOrder.s_CreateOrder(mainToken);
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
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
            resOld = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(resOld);
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));

        int i = 1;
        while (i<3){
            String orderId = CreateOrder.s_CreateOrder(mainToken);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            Order_RecommendDoctor.s_RecommendDoctor(orderId,"3721");
            i++;
        }
        //s_CreateOrder.s_CreateOrder(mainToken);
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
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
            resOld = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(resOld);
        int count = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(parseJson(data,"doctor:status_count:agentId:handling"));
        int pendingpayment = Integer.parseInt(parseJson(data, "doctor:status_count:agentId:pendingpayment"));

        int  i= 1;
        while (i<4){
            String orderId = CreateOrder.s_CreateOrder(mainToken);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            Order_RecommendDoctor.s_RecommendDoctor(orderId,"3721");
            Order_ThreewayCall_V2.s_CallV2(orderId,"success");
            i++;
        }
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotNull(parseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(parseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling),"处理中的订单数不正确");
        Assert.assertEquals(parseJson(data, "doctor:status_count:agentId:pendingpayment"), String.valueOf(pendingpayment+i-1),"待支付的订单数不正确");
        Reporter.log("我就是玩一下");
    }

    @Test
    public void test_09_测试返回的地区服务专员(){
        //TODO
        logger.debug("我就是玩二下");
        logger.info("test testlogger");
        logger.info("test testlogger");
        logger.error("test testlogger");
    }
}
