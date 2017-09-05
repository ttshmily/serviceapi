package com.mingyizhudao.qa.functiontest.patient;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.login.MobileProfile;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by TianJing on 2017/9/1.
 */
public class PatientSendVerifyCode extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String url = "/appointment/api/v1/sendVerifyCode";

    public static void s_SendVerifyCode(String phone){
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject mobile = new JSONObject();
        mobile.put("mobile", phone);
        logger.info("请求验证码到手机号" + phone + " ...") ;
        res = HttpRequest.s_SendPost(host_patient + url, mobile.toString(), "");
        logger.info("mobile是: " + phone + "...请发送验证码到服务器进行验证");
    }

    @Test
    public void test_01_填写错误手机号发送验证码成功失败(){
       //s_SendVerifyCode("18317186256");
        String res = "";
        MobileProfile body = new MobileProfile(false);
        System.out.println(body.body.toString());
        body.body.replace("mobile", "");
        res = HttpRequest.s_SendPost(host_patient + url,body.body.toString(), "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

    @Test
    public void test_02_填写正确手机号发送验证码成功(){
        String res = "";
        MobileProfile body = new MobileProfile(true);
        System.out.println(body.body.toString());
        res = HttpRequest.s_SendPost(host_patient + url, body.body.toString(), "");
        //System.out.println(res);
        s_CheckResponse(res);
        Assert.assertEquals(code,"1000000","消息码不正确");
    }

    @Test
    public void test_03_填写错误少于11位的手机号发送验证码失败() {
        String res = "";
        MobileProfile body = new MobileProfile(false);
        System.out.println(body.body.toString());
        body.body.replace("mobile", "1330000000");
        System.out.println(body.body.toString());
        res = HttpRequest.s_SendPost(host_patient + url,body.body.toString(), "");
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }


}
