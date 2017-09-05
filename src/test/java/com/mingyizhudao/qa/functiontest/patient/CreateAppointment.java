package com.mingyizhudao.qa.functiontest.patient;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentOrder;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by TianJing on 2017/9/1.
 */
public class CreateAppointment extends BaseTest{
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String url = "/appointment/api/v1/orders";
    public static String mobile;

    public static String s_CreateOrder(){
        AppointmentOrder ap = new AppointmentOrder("patient");
        String res = HttpRequest.s_SendPost(host_patient + url, ap.transform(), "");
        return res;
    }

    @Test
    public void test_01_创建预约单保存(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");
        String patient_phone = ap.getPatient_phone();
        System.out.println(patient_phone);

        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        String order_number = JSONObject.fromObject(res).getJSONObject("data").getString("order_number");
        logger.info("order_number: " + order_number);

        logger.info("查看刚刚创建的订单详情");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "patient_name"), ap.getPatient_name(), "患者姓名存储不正确");
        Assert.assertEquals(s_ParseJson(data, "patient_gender"),String.valueOf(ap.getPatient_gender()), "患者性别存储不正确");
        Assert.assertEquals(s_ParseJson(data, "patient_age"),String.valueOf(ap.getPatient_age()), "患者年龄存储不正确");
        Assert.assertEquals(s_ParseJson(data, "patient_phone"),ap.getPatient_phone(), "患者手机号存储不正确");
        Assert.assertEquals(s_ParseJson(data, "major_disease_id"),String.valueOf(ap.getMajor_disease_id()), "确诊疾病id存储不正确");
        Assert.assertEquals(s_ParseJson(data, "major_disease_name"), Generator.diseaseName(String.valueOf(ap.getMajor_disease_id())), "确诊疾病名称存储不正确");
        Assert.assertEquals(s_ParseJson(data, "disease_description"), ap.getDisease_description(), "疾病描述存储不正确");
        Assert.assertEquals(s_ParseJson(data, "source_type"), ap.getSource_type(), "订单来源存储不正确");
        Assert.assertEquals(s_ParseJson(data,"type"), "2", "订单类型应该为2");
        Assert.assertEquals(s_ParseJson(data, "status"), "1000", "订单状态应该为1000待领取");
        Assert.assertEquals(s_ParseJson(data, "is_test"), "false", "订单为应该为真实数据");
    }

    @Test
    public void test_02_创建预约单_缺少患者姓名(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setPatient_name("");
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "姓名不能为空");

        ap.setPatient_name(null);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "姓名不能为空");
    }

    @Test
    public void test_03_创建预约单_缺少患者性别或性别不正确(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setPatient_gender(null);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "性别不能为空");

        ap.setPatient_gender(0);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "性别不正确");
    }

    @Test
    public void test_04_创建预约单_缺少患者年龄(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setPatient_age(null);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "年龄填写不能为空");
    }

    @Test
    public void test_05_创建预约单_缺少手机号(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setPatient_phone(null);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "手机号不能为空");

        ap.setPatient_phone("");
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "手机号不能为空");
    }

    @Test
    public void test_06_创建预约单_缺少验证码或验证码错误(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setCode("");
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "验证码不能为空");

        ap.setCode("121212");
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "验证码不正确");
    }

    @Test
    public void test_07_创建预约单_缺少确诊疾病(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setMajor_disease_id("");
        ap.setMajor_disease_name("");
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "确诊疾病不能为空");

        ap.setMajor_disease_id(null);
        ap.setMajor_disease_name(null);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "确诊疾病不能为空");
    }

    @Test
    public void test_08_创建预约单_缺少疾病描述(){
        String res = "";
        AppointmentOrder ap = new AppointmentOrder("patient");

        ap.setDisease_description("");
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "疾病描述不能为空");

        ap.setDisease_description(null);
        res = HttpRequest.s_SendPost(host_patient + url, ap.transform(),"");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "疾病描述不能为空");
    }
}
