package com.mingyizhudao.qa.common;


import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.functiontest.doctor.GetDoctorProfile_V1;
import com.mingyizhudao.qa.functiontest.login.CheckVerifyCode;
import com.mingyizhudao.qa.functiontest.login.SendVerifyCode;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_CertifySync_V2;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Certify_V2;
import com.mingyizhudao.qa.functiontest.doctor.UpdateDoctorProfile_V1;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
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

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String protocol = "";
    public static String host_doc = "";
    public static String host_crm = "";
    public static String host_login = "";
    public static String host_kb = "";
    public static String host_bda = "";
    public static String host_appointment = "";
    public static String crm_token = "";
    public static String bda_token = "";
    public static String bda_token_staff = "";
    public static String init_kb = "";
    public static String designatedDoctor_token = "";


    public static String mainMobile = "";
    public static String mainToken = "";
    public static String mainDoctorId = "";
    public static String mainDoctorName = "";
    public static String mainDoctorHospitalId = "";
    public static String mainDoctorHospitalName = "";
    public static String mainExpertId = "";
    public static DoctorProfile mainDP;

    public static String mainOperatorId = "";
    public static String mainOperatorName = "";

    public String code = "";
    public String message = "";
    public JSONObject data;

    public static void main(String[] args) {
//        String mobile = SendVerifyCode.s_Send();
//        String token = CheckVerifyCode.s_Check();
//        s_CreateRegistered();
//        s_CreateRegisteredDoctor();
    }

    static {
        PropertyConfigurator.configure(BaseTest.class.getClassLoader().getResource("log4j.properties"));
//         读取配置文件
        Properties prop = new Properties();
        try {
            InputStream in = BaseTest.class.getClassLoader().getResourceAsStream("environment.properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            logger.error(e.toString());
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
            host_appointment = prop.getProperty("host_appointment", "services.dev.myzd.info/ims");
            mainOperatorId = prop.getProperty("mainOperatorId", "chao.fang@mingyizhudao.com");
            mainOperatorName = prop.getProperty("mainOperatorName");


            crm_token = prop.getProperty("crm_token");
            bda_token = prop.getProperty("bda_token");
            bda_token_staff = prop.getProperty("bda_token_staff");
            init_kb = prop.getProperty("init_kb", "false");
            designatedDoctor_token = prop.getProperty("designatedDoctor_token");

            host_doc = protocol.concat("://").concat(host_doc);
            host_crm = protocol.concat("://").concat(host_crm);
            host_login = protocol.concat("://").concat(host_login);
            host_kb = protocol.concat("://").concat(host_kb);
            host_bda = protocol.concat("://").concat(host_bda);
            host_appointment = protocol.concat("://").concat(host_appointment);
        }
    }

    public static String s_JobName() {
        String jobName = "";
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement s = stack[i];
            if (s.getMethodName().startsWith("s_")) {
                continue;
            } else {
                jobName = s.getClassName();
                break;
            }
        }
        return jobName;
    }

    @BeforeSuite
    public void SetUpSuite() throws Exception {
        KnowledgeBase.s_Init();
        crm_token = JSONObject.fromObject(HttpRequest.s_SendGet("http://services.dev.myzd.info/crm/api/internal/devToken" , "email="+mainOperatorId+"&name=test", "")).getJSONObject("data").getString("token");
        bda_token = JSONObject.fromObject(HttpRequest.s_SendGet("http://work.myzd.info/wx/internal/api/dev-tokens" , "", "")).getJSONObject("data").getJSONObject("chao.fang@mingyizhudao.com").getString("token");
        bda_token_staff = JSONObject.fromObject(HttpRequest.s_SendGet("http://work.myzd.info/wx/internal/api/dev-tokens" , "", "")).getJSONObject("data").getJSONObject("lei.wang@mingyizhudao.com").getString("token");
        mainDP = new DoctorProfile(true);
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainDP);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        mainMobile = mainDoctorInfo.get("mobile");
        mainToken = mainDoctorInfo.get("token");
        mainDoctorId = mainDoctorInfo.get("id");
        mainDoctorName = mainDP.body.getJSONObject("doctor").getString("name");
        mainDoctorHospitalId = mainDoctorInfo.get("hospitalId");
        mainDoctorHospitalName = Generator.hospitalName(mainDoctorHospitalId);
        mainExpertId = mainDoctorInfo.get("expert_id");

        logger.info("初始化信息完成，准备执行用例");
        logger.info("mainDoctorId为:\t"+mainDoctorId);
        logger.info("mainDoctorName为:\t"+mainDoctorName);
        logger.info("mainDoctorToken为:\t"+mainToken);
        logger.info("mainDoctorHospitalId为:\t"+mainDoctorHospitalId);
        logger.info("mainDoctorHospitalName为:\t"+mainDoctorHospitalName);
        logger.info("mainExpertId为:\t"+mainExpertId);
        logger.info("mainOperatorId为:\t"+mainOperatorId);
        logger.info("crm_token为:\t"+crm_token);
        logger.info("bda_token为:\t"+bda_token);
        logger.info("bda_token_staff为:\t"+bda_token_staff);
    }

    @BeforeClass
    public void SetUpClass() throws Exception {
        TestLogger logger = new TestLogger(getClass().getName());
        logger.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////// ");
        logger.info("//    TestAPI START:\t" + getClass().getSimpleName());
        logger.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////// \n");

    }

    @AfterClass
    public void TearDownClass() throws Exception {
        TestLogger logger = new TestLogger(getClass().getName());
        logger.info("============================================================================================================= ");
        logger.info("||    TestAPI END:\t" + getClass().getSimpleName());
        logger.info("============================================================================================================= \n");
    }

    @BeforeMethod
    public void SetUpTC(Method method) throws Exception {
        TestLogger logger = new TestLogger(getClass().getName());
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        logger.info("||    TestCase START:\t" + method.getName());
    }

    @AfterMethod
    public void TearDownTC(Method method) throws Exception {
        TestLogger logger = new TestLogger(getClass().getName());
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
        logger.info("||\t TestCase END:\t" + method.getName());
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \n");
    }

/*    public void TestDescription(String testName) {
        TestLogger logger = new TestLogger(getClass().getName());
        logger.info("||\t\t Step 1");
        logger.info("||\t\t Step 2");
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n");
    }*/

    public void s_CheckResponse(String res) {
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject json = null;
        try {
            json = JSONObject.fromObject(res);
        } catch (JSONException e) {
            logger.error("res is NOT a JSON");
            logger.error(res);
            this.code = null;
            this.data = null;
            this.message = null;
            return;
        }
        this.code = json.getString("code");
        this.message = json.getString("message");
        if (json.containsKey("data")) {
            String tmp = json.getString("data");
            if (tmp.equals(""))
                this.data = JSONObject.fromObject("{}");
            else if (tmp.equals("[]"))
                this.data = JSONObject.fromObject("{}");
            else if (this.code.equals("1000000"))
                this.data = json.getJSONObject("data");
            else
                this.data = null;
        } else {
            this.data = null;
        }
        logger.info("[ code ]:\t" + code);
        logger.info("[ message ]:\t" + message);
        logger.info("[ data ]:\t" + data);
    }

//    生成一个医生用户
    protected static HashMap<String, String> s_CreateRegistered() {
        TestLogger logger = new TestLogger(s_JobName());
        logger.info("创建注册用户...");
        String mobile = SendVerifyCode.s_Send();
        String token = CheckVerifyCode.s_Check();

        String res = GetDoctorProfile_V1.s_MyProfile(token);
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

//    创建一个医生，并且完善信息
    protected static HashMap<String, String> s_CreateRegisteredDoctor(DoctorProfile dp) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String,String> info = s_CreateRegistered();
        if (info == null) return null;
        String token = info.get("token");

        logger.info("更新医生信息...");
        UpdateDoctorProfile_V1.s_Update(token, dp);
        String res = GetDoctorProfile_V1.s_MyProfile(token);
        String doctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
        if (doctorHospitalId == null || doctorHospitalId.isEmpty()) {
            logger.error("更新失败，医生信息不完整");
            return null;
        }
        info.put("hospitalId", doctorHospitalId);
        logger.info("doctorName为:\t"+dp.body.getJSONObject("doctor").getString("name"));
        logger.info("doctorHospitalId为:\t"+doctorHospitalId);
        logger.info("doctorHospitalName为:\t"+ Generator.hospitalName(doctorHospitalId));
        return info;
    }

//    创建一个医生并且认证
    protected static HashMap<String, String> s_CreateVerifiedDoctor(DoctorProfile dp) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String,String> info = s_CreateRegisteredDoctor(dp);
        if (info == null) return null;

        logger.info("认证医生...");
        String doctorId = info.get("id");

        RegisteredDoctor_Certify_V2.s_CertifyOnly(doctorId, "1");
        String is_verified = RegisteredDoctor_Certify_V2.s_CertifyOnly(doctorId, "1");
        if (!is_verified.equals("1")) {
            logger.error("认证失败");
            return null;
        }
        info.put("is_verified", is_verified);
        logger.info("is_verified为:\t"+is_verified);
        return info;
    }

//    创建一个医生并且认证和同步
    protected static HashMap<String, String> s_CreateSyncedDoctor(DoctorProfile dp) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String,String> info = s_CreateRegisteredDoctor(dp);
        if (info == null) return null;
        logger.info("认证并同步医生...");
        String doctorId = info.get("id");
        HashMap<String,String> tmp = RegisteredDoctor_CertifySync_V2.s_CertifyAndSync(doctorId, "1");
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
