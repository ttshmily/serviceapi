package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.SurgeryOrder;
import com.mingyizhudao.qa.dataprofile.User;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by ttshmily on 7/4/2017.
 */
public class CreateOrder extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/createorder";

    public static String s_CreateOrder(String token){

        SurgeryOrder su = new SurgeryOrder("order");
        String res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), token);
        return JSONObject.fromObject(res).getJSONObject("data").getString("order_number");
    }

    public static String s_CreateOrder(String token, SurgeryOrder su){
        String res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), token);
        return JSONObject.fromObject(res).getJSONObject("data").getString("order_number");
    }

    @Test
    public void test_01_创建订单_信息齐备_已认证用户() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        String orderId = s_ParseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");

        logger.info("查看刚刚创建的订单详情");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "patient_name"), su.getOrder().getPatient_name(), "患者姓名存储不正确");
        Assert.assertEquals(s_ParseJson(data, "patient_gender"), String.valueOf(su.getOrder().getPatient_gender()), "患者性别字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "patient_age"), String.valueOf(su.getOrder().getPatient_age()), "患者年龄字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "patient_phone"), su.getOrder().getPatient_phone(), "患者手机号字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "major_disease_id"), String.valueOf(su.getOrder().getMajor_disease_id()), "主诉疾病ID字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "major_disease_name"), Generator.diseaseName(String.valueOf(su.getOrder().getMajor_disease_id())), "主诉疾病名称不正确");
        Assert.assertEquals(s_ParseJson(data, "minor_disease_id"), String.valueOf(su.getOrder().getMinor_disease_id()), "次诉疾病ID字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "minor_disease_name"), Generator.diseaseName(String.valueOf(su.getOrder().getMinor_disease_id())), "次诉疾病名称不正确");
        Assert.assertEquals(s_ParseJson(data, "diagnosis"), su.getOrder().getDiagnosis(), "病例描述字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "expected_surgery_start_date"), String.valueOf(su.getOrder().getExpected_surgery_start_date()), "期望手术最早开始时间字段存储不正确");
        Assert.assertEquals(s_ParseJson(data, "expected_surgery_due_date"), String.valueOf(su.getOrder().getExpected_surgery_due_date()), "期望手术最晚开始时间字段存储不正确");

        Assert.assertEquals(s_ParseJson(data, "expected_surgery_hospital_id"), String.valueOf(su.getOrder().getExpected_surgery_hospital_id()), "期望医院ID存储不正确");
        Assert.assertEquals(s_ParseJson(data, "expected_surgery_hospital_name"), Generator.hospitalName(String.valueOf(su.getOrder().getExpected_surgery_hospital_id())), "期望医院名称字段不正确");
        Assert.assertEquals(s_ParseJson(data, "status"), "1000", "新建订单状态应当为1000");
        //Assert.assertEquals(s_ParseJson(data,"OrderStatusText"), "待处理", "新建订单状态描述应当为'处理中'");
        Assert.assertNotEquals(s_ParseJson(data, "created_at"), "", "订单创建时间字段缺失");
        Assert.assertEquals(s_ParseJson(data, "order_number"), orderId, "订单号字段不正确");
        Assert.assertNotNull(s_ParseJson(data, "medical_record_pictures()"), "病例图片字段缺失");
        //Assert.assertEquals(s_ParseJson(data,"medical_record_pictures"),String.valueOf(su.getOrder().getMedical_record_pictures()),"病例图片url字段缺失");
        //Assert.assertNotNull(s_ParseJson(data,"medical_record_pictures(0):url"), "病例图片url字段缺失");
        //Assert.assertNotNull(s_ParseJson(data,"medical_record_pictures(1):url"), "病例图片url字段缺失");
        //Assert.assertEquals(s_ParseJson(data,"medical_record_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        //Assert.assertEquals(s_ParseJson(data,"medical_record_pictures(1):key"), "2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg");
        Assert.assertNotNull(s_ParseJson(data, "agent_contact_id"));
        Assert.assertEquals(s_ParseJson(data, "agent_referrer_id"), "SH0133"); //北京大学口腔医院, 北京，区域服务人员 - 方超 - SH0133
        Assert.assertEquals(s_ParseJson(data, "agent_referrer_name"), "方超"); //北京大学口腔医院, 北京，区域服务人员 - 方超 - SH0133
        Assert.assertNotNull(s_ParseJson(data, "agent_city_id"));
        Assert.assertNotNull(s_ParseJson(data, "agent_city_name"));
        Assert.assertEquals(s_ParseJson(data, "agent_hospital"), "北京大学口腔医院");
        Assert.assertNotNull(s_ParseJson(data, "agent_referrer_group_id"));
    }

    @Test
    public void test_02_创建订单_信息齐备_未登录用户() {

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");
        res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        Assert.assertEquals(code, "2210304");
    }

    @Test
    public void test_03_创建订单_缺少患者姓名不可以创建() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        su.getOrder().setPatient_name("");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        su.getOrder().setPatient_name("abcdefghijklmnopqrstuvwxyz一二三四五六七八九十甲乙丙地子卯寅丑");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        su.getOrder().setPatient_name(null);
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_04_创建订单_缺少患者性别或性别不正确不可以创建() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        su.getOrder().setPatient_gender(3);
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

        su.getOrder().setPatient_gender(null);
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "性别为3不应该能创建病历");

    }

    @Test
    public void test_05_创建订单_缺少患者年龄不可以创建() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        su.getOrder().setPatient_age(null);
        res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_06_创建订单_缺少患者手机不可以创建() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        su.getOrder().setPatient_phone("");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code,"1000000");// PD要求可以创建。。。

        su.getOrder().setPatient_phone(null);
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000"); // PD要求可以创建。。。
    }

    @Test
    public void test_07_创建订单_缺少主诉疾病不可以创建() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        su.getOrder().setMajor_disease_id("");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code,"1000000");

        su.getOrder().setMajor_disease_id(null);
        res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");

    }

    @Test
    public void test_08_创建订单_缺少次诉疾病可以创建() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        su.getOrder().setMinor_disease_id("");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code,"1000000");

        su.getOrder().setMinor_disease_id(null);
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

/*    @Test
    public void test_09_创建订单_信息齐备_认证中的医生没有邀请者可以创建() {

        String res = "";

        User dp = new User();
        dp.getDoctor().setInviter_no(null);
        dp.getDoctor().setHospital_id("98");//常州市武进人民医院, 常州，区域服务人员 - 方超
        HashMap<String, String> doc = s_CreateRegisteredDoctor(dp);
        if (doc == null) {
            Assert.fail("创建医生失败");
        }
        String tmpToken = doc.get("token");

        SurgeryOrder su = new SurgeryOrder("order");
        res = HttpRequest.s_SendPost(host_doc+uri, JSONObject.fromObject(su).toString(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000"); // PD要求认证中的医生也可以创建了。。。

    }*/

    @Test
    public void test_10_创建订单_信息齐备_认证中的医生有邀请者可以创建() {

        String res = "";

        HashMap<String, String> doc = s_CreateRegisteredDoctor(new User());
        if (doc == null) {
            Assert.fail("创建医生失败");
        }
        String tmpToken = doc.get("token");

        SurgeryOrder su = new SurgeryOrder("order");
        res = HttpRequest.s_SendPost(host_doc+uri, JSONObject.fromObject(su).toString(), tmpToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_11_创建订单_信息齐备_期望手术医院不传() {

        String userToken = "";
        String userDoctorHospitalId = "";
        String userDoctorHospitalName = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");
        userDoctorHospitalId = mainDoctorInfo.get("hospitalId");
        userDoctorHospitalName = Generator.hospitalName(userDoctorHospitalId);

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");

        logger.info("不传入期望手术医院的ID。。。");
        su.getOrder().setExpected_surgery_hospital_id("");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        String orderId = Helper.s_ParseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");
        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.s_MyInitiateOrder(userToken, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data,"order:expected_surgery_hospital_id"), userDoctorHospitalId);
        Assert.assertEquals(Helper.s_ParseJson(data,"order:expected_surgery_hospital_name"), userDoctorHospitalName);

        logger.info("传入期望手术医院的ID=0。。。");
        su.getOrder().setExpected_surgery_hospital_id("0");
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        orderId = Helper.s_ParseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");
        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.s_MyInitiateOrder(userToken, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data,"order:expected_surgery_hospital_id"), "0");
        Assert.assertEquals(Helper.s_ParseJson(data,"order:expected_surgery_hospital_name"), "待回访");

        logger.info("不传入期望手术医院的key。。。");
        su.getOrder().setExpected_surgery_hospital_id(null);
        res = HttpRequest.s_SendPost(host_doc+uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "创建订单失败");
        orderId = Helper.s_ParseJson(data, "order_number");
        Assert.assertNotEquals(orderId, "", "返回的订单ID格式有误");
        logger.info("查看刚刚创建的订单详情");
        res = GetOrderDetail_V1.s_MyInitiateOrder(userToken, orderId);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(Helper.s_ParseJson(data,"order:expected_surgery_hospital_id"), userDoctorHospitalId);
        Assert.assertEquals(Helper.s_ParseJson(data,"order:expected_surgery_hospital_name"), userDoctorHospitalName);
    }

    @Test
    public void test_12_创建订单_病例图片作为非必填字段() {

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        SurgeryOrder su = new SurgeryOrder("order");
        res = HttpRequest.s_SendPost(host_doc + uri, su.transform(), userToken);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

}

