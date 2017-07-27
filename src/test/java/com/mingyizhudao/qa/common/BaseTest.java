package com.mingyizhudao.qa.common;


import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_CertifySync_V2;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Certify_V2;
import com.mingyizhudao.qa.testcase.doctor.*;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by ttshmily on 20/3/2017.
 */
public class BaseTest {

    public static final Logger logger= Logger.getLogger(BaseTest.class);
    public static String protocol = "";
    public static String host_doc = "";
    public static String host_crm = "";
    public static String host_login = "";
    public static String host_kb = "";
    public static String host_bda = "";
    public static String crm_token = "";
    public static String bda_token = "";
    public static String bda_token_staff = "";

    public static String mainMobile = "";
    public static String mainToken = "";
    public static String mainDoctorId = "";
    public static String mainDoctorName = "";
    public static String mainDoctorHospitalId = "";
    public static String mainDoctorHospitalName = "";
    public static String mainExpertId = "";
    public static DoctorProfile mainDP;
    public static String mainBD = "SH0133";

    public static String mainOperatorId = "";
    public static String mainOperatorName = "";

    public String code = "";
    public String message = "";
    public JSONObject data;

//    public static void main(String[] args) {
//        String mobile = SendVerifyCode.send();
//        String token = CheckVerifyCode.check();
//        CreateRegistered();
//        CreateRegisteredDoctor();
//    }
    static  {
        PropertyConfigurator.configure(BaseTest.class.getClassLoader().getResource("log4j.properties"));
        // 读取配置文件
        Properties prop = new Properties();
        try {
            InputStream in = BaseTest.class.getClassLoader().getResourceAsStream("environment.properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            logger.error(e);
            logger.error("初始化配置失败，退出...");
            System.exit(1);
        }

        {
            // 初始化配置文件中的变量
            protocol = prop.getProperty("protocol", "http");

            host_doc = prop.getProperty("host_doc", "services.dev.myzd.info/doctor");
            host_crm = prop.getProperty("host_crm", "services.dev.myzd.info/crm");
            host_bda = prop.getProperty("host_bda", "services.dev.myzd.info/bd-assistant");
            host_login = prop.getProperty("host_login", "login.dev.myzd.info");
            host_kb = prop.getProperty("host_kb", "192.168.33.1");

            crm_token = prop.getProperty("crm_token");
            bda_token = prop.getProperty("bda_token");
            bda_token_staff = prop.getProperty("bda_token_staff");
            mainOperatorId = prop.getProperty("mainOperatorId");
            mainOperatorName = prop.getProperty("mainOperatorName");

            host_doc = protocol.concat("://").concat(host_doc);
            host_crm = protocol.concat("://").concat(host_crm);
            host_login = protocol.concat("://").concat(host_login);
            host_kb = protocol.concat("://").concat(host_kb);
            host_bda = protocol.concat("://").concat(host_bda);
        }
    }

    @BeforeSuite
    public void setUpSuite() throws Exception {
        KB.init();
        crm_token = JSONObject.fromObject(HttpRequest.sendGet("http://services.dev.myzd.info/crm/api/internal/devToken" , "email="+mainOperatorId, "")).getJSONObject("data").getString("token");
//        bda_token = JSONObject.fromObject(HttpRequest.sendGet("http://work.myzd.info/wx/internal/api/dev-tokens" , "", "")).getJSONObject("data").getJSONObject("chao.fang@mingyizhudao.com").getString("token");
//        bda_token_staff = JSONObject.fromObject(HttpRequest.sendGet("http://work.myzd.info/wx/internal/api/dev-tokens" , "", "")).getJSONObject("data").getJSONObject("lei.wang@mingyizhudao.com").getString("token");
        mainDP = new DoctorProfile(true);
        HashMap<String,String> mainDoctorInfo = CreateSyncedDoctor(mainDP);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        mainMobile = mainDoctorInfo.get("mobile");
        mainToken = mainDoctorInfo.get("token");
        mainDoctorId = mainDoctorInfo.get("id");
        mainDoctorName = mainDP.body.getJSONObject("doctor").getString("name");
        mainDoctorHospitalId = mainDoctorInfo.get("hospitalId");
        mainDoctorHospitalName = UT.hospitalName(mainDoctorHospitalId);
        mainExpertId = mainDoctorInfo.get("expert_id");

        logger.info("mainDoctorId为:\t"+mainDoctorId);
        logger.info("mainDoctorName为:\t"+mainDoctorName);
        logger.info("mainDoctorToken为:\t"+mainToken);
        logger.info("mainDoctorHospitalId为:\t"+mainDoctorHospitalId);
        logger.info("mainDoctorHospitalName为:\t"+mainDoctorHospitalName);
        logger.info("mainExpertId为:\t"+mainExpertId);

        logger.info("mainOperatorId为:\t"+mainOperatorId);
        logger.info("crm_token为:\t"+crm_token);
    }

    @BeforeClass
    public void setUpClass() throws Exception {

        logger.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////// ");
        logger.info("//    TestAPI START:\t" + getClass().getSimpleName());
        logger.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////// \n");

    }

    @AfterClass
    public void tearDownClass() throws Exception {

        logger.info("Test Cleaning...");
        logger.info("mainDoctorId为"+mainDoctorId);
        logger.info("恢复医生信息：");
        UpdateDoctorProfile_V1.updateDoctorProfile(mainToken, mainDP);
        logger.info("============================================================================================================= ");
        logger.info("||    TestAPI END:\t" + getClass().getSimpleName());
        logger.info("============================================================================================================= \n");

    }

    @BeforeMethod
    public void setUpTC(Method method) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        logger.info("||    TestCase START:\t" + method.getName());
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n");
    }

    @AfterMethod
    public void tearDownTC(Method method) {
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
        logger.info("||    TestCase END:\t" + method.getName());
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \n");
    }

    public void checkResponse(String res) throws JSONException {
        JSONObject json = JSONObject.fromObject(res);
        this.code = json.getString("code");
        this.message = json.getString("message");
        if (json.containsKey("data")) {
            if (json.getString("data").equals(""))
                this.data = JSONObject.fromObject("{}");
            else if (json.getString("data").equals("[]"))
                this.data = JSONObject.fromObject("{}");
            else
                if (this.code.equals("1000000")) this.data = json.getJSONObject("data"); else this.data = null;
        } else {
            this.data = null;
        }
        logger.info("<<<<<< [ code ]:\t" + code);
        logger.info("<<<<<< [ message ]:\t" + message);
        logger.info("<<<<<< [ data ]:\t" + data);
    }

//    生成一个医生用户
    public HashMap<String, String> CreateRegistered() {
        logger.info("创建注册用户...");
        String mobile = SendVerifyCode.send();
        String token = CheckVerifyCode.check();

        String res = GetDoctorProfile_V1.MyProfile(token);
        String doctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        if( doctorId == null || doctorId.isEmpty()) {
            logger.error("创建注册用户失败");
            return null;
        }
        HashMap<String, String> result = new HashMap<>();
        result.put("id", doctorId);
        result.put("mobile", mobile);
        result.put("token", token);
        logger.info("mobile为:\t"+mobile);
        logger.info("token为:\t"+token);
        logger.info("doctorId为:\t"+doctorId);
        return result;
    }

//创建一个医生，并且完善信息
    public HashMap<String, String> CreateRegisteredDoctor(DoctorProfile dp) {
        HashMap<String,String> info = CreateRegistered();
        if (info == null) return null;
        String token = info.get("token");

        logger.info("更新医生信息...");
        UpdateDoctorProfile_V1.updateDoctorProfile(token, dp);
        String res = GetDoctorProfile_V1.MyProfile(token);
        String doctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
        if (doctorHospitalId == null || doctorHospitalId.isEmpty()) {
            logger.error("更新失败，医生信息不完整");
            return null;
        }
        info.put("hospitalId", doctorHospitalId);
        logger.info("doctorName为:\t"+dp.body.getJSONObject("doctor").getString("name"));
        logger.info("doctorHospitalId为:\t"+doctorHospitalId);
        logger.info("doctorHospitalName为:\t"+ UT.hospitalName(doctorHospitalId));
        return info;
    }

// 创建一个医生并且认证
    public HashMap<String, String> CreateVerifiedDoctor(DoctorProfile dp) {
        HashMap<String,String> info = CreateRegisteredDoctor(dp);
        if (info == null) return null;

        logger.info("认证医生...");
        String doctorId = info.get("id");

        RegisteredDoctor_Certify_V2.CertifyOnly(doctorId, "1");
        String is_verified = RegisteredDoctor_Certify_V2.CertifyOnly(doctorId, "1");
        if (!is_verified.equals("1")) {
            logger.error("认证失败");
            return null;
        }
        info.put("is_verified", is_verified);
        logger.info("is_verified为:\t"+is_verified);
        return info;
    }

// 创建一个医生并且认证和同步
    public HashMap<String, String> CreateSyncedDoctor(DoctorProfile dp) {
        HashMap<String,String> info = CreateRegisteredDoctor(dp);
        if (info == null) return null;
        logger.info("认证并同步医生...");
        String doctorId = info.get("id");
        HashMap<String,String> tmp = RegisteredDoctor_CertifySync_V2.CertifyAndSync(doctorId, "1");
        if (!tmp.get("is_verified").equals("1") || tmp.get("kb_id") == null) {
            logger.error("认证/同步医生失败");
            return null;
        }
        info.put("is_verified", tmp.get("is_verified"));
        info.put("expert_id", tmp.get("kb_id"));
        logger.info("is_verified为:\t"+tmp.get("is_verified"));
        logger.info("expert_id为:\t"+tmp.get("kb_id"));
        return info;
    }

/*
    info.get("id") - user_id
    info.get("token") - token
    info.get("mobile") - mobile
    info.get("hospitalId")
    info.get("is_verified")
    info.get("expert_id")
    */

}
