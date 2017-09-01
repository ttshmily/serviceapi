package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ReceiveTask;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_RecommendDoctor;
import com.mingyizhudao.qa.functiontest.crm.trading.surgery.Order_ThreewayCall_V2;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

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
        res = HttpRequest.s_SendGet(host_doc +uri, "", token);
        return res;
    }

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");

    }

    @Test
    public void test_02_没有token信息的请求不能获得个人信息并返回正确的错误提示() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc+uri,"", "");
    //    logger.info(unicodeString(res));
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000","没有登录信息，不应该返回data");

    }

    @Test
    public void test_03_错误token的请求不能获得个人信息并返回正确的错误提示() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", "nidawoya");
        //    logger.info(unicodeString(res));
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000","错误token信息，不应该返回data");

    }

    @Test
    public void test_04_测试data字段返回了足够的医生信息() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_required"), "is_required字段缺失");
    }

    //created by tianjing on 2017/6/27
    @Test
    public void test_05_测试总订单数_测试处理中订单数_CRM未领取(){

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        CreateOrder.s_CreateOrder(userToken);
        String resOld = "";
        resOld = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(resOld);
        int count = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:handling"));
        System.out.println("总订单数：" + count);
        System.out.println("处理中订单数：" + countHandling);

        int i = 1;
        while (i<3){
            CreateOrder.s_CreateOrder(userToken);
            i++;
        }
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling+i-1),"处理中的订单数不正确");
    }

    @Test
    public void test_06_测试总订单数_测试处理中订单数_CRM已领取(){

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        CreateOrder.s_CreateOrder(userToken);
        String resOld = "";
        resOld = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(resOld);

        int count = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:handling"));
        System.out.println("总订单数：" + count);
        System.out.println("处理中订单数：" + countHandling);

        int i = 1;
        while (i<2){
            String orderId = CreateOrder.s_CreateOrder(userToken);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            i++;
        }
        //s_CreateOrder.s_CreateOrder(mainToken);
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling+i-1),"处理中的订单数不正确");
    }

    @Test
    public void test_07_测试总订单数_测试处理中订单数_推荐完医生(){

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        CreateOrder.s_CreateOrder(userToken);
        String resOld = "";
        resOld = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(resOld);
        int count = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:handling"));

        int i = 1;
        while (i<3){
            String orderId = CreateOrder.s_CreateOrder(mainToken);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            Order_RecommendDoctor.s_RecommendDoctor(orderId,"3721");
            i++;
        }
        //s_CreateOrder.s_CreateOrder(mainToken);
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling+i-1),"处理中的订单数不正确");
    }

    @Test
    public void test_08_测试总订单数_待支付状态的订单数(){

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        CreateOrder.s_CreateOrder(userToken);
        String resOld = "";
        resOld = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(resOld);
        int count = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:count"));
        int countHandling = Integer.parseInt(s_ParseJson(data,"doctor:status_count:agentId:handling"));
        int pendingpayment = Integer.parseInt(s_ParseJson(data, "doctor:status_count:agentId:pendingpayment"));

        int  i= 1;
        while (i<4){
            String orderId = CreateOrder.s_CreateOrder(userToken);
            Order_ReceiveTask.s_ReceiveTask(orderId);
            Order_RecommendDoctor.s_RecommendDoctor(orderId,"3721");
            Order_ThreewayCall_V2.s_CallV2(orderId,"success");
            i++;
        }
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", userToken);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data,"doctor"),"doctor字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:name"), "name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:inviter_name"), "inviter_name字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:is_verified"),"is_verified字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:mobile"), "mobile字段缺失");
        Assert.assertNotNull(s_ParseJson(data,"doctor:hospital_name"), "hospital_name字段缺失");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:count"), String.valueOf(count+i-1),"总订单数不正确");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:handling"), String.valueOf(countHandling),"处理中的订单数不正确");
        Assert.assertEquals(s_ParseJson(data, "doctor:status_count:agentId:pendingpayment"), String.valueOf(pendingpayment+i-1),"待支付的订单数不正确");
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
