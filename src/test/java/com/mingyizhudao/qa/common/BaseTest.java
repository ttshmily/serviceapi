package com.mingyizhudao.qa.common;


import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Certify;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Certify_V2;
import com.mingyizhudao.qa.testcase.doctor.*;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
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
    public static String mainOperatorRole = "";

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
            protocol = prop.getProperty("protocol");
            crm_token = prop.getProperty("crm_token");
            bda_token = prop.getProperty("bda_token");

            host_doc = prop.getProperty("host_doc");
            host_crm = prop.getProperty("host_crm");
            host_login = prop.getProperty("host_login");
            host_kb = prop.getProperty("host_kb");
            host_bda = prop.getProperty("host_bda");

            crm_token = prop.getProperty("crm_token");
            bda_token = prop.getProperty("bda_token");
            bda_token_staff = prop.getProperty("bda_token_staff");

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
        mainMobile = SendVerifyCode.send();
        mainToken = CheckVerifyCode.check();
        if (mainToken.isEmpty() || mainToken == null) {
            logger.error("初始化失败，退出");
            System.exit(1000);
        }

        String res = GetDoctorProfile.getDoctorProfile(mainToken);
        mainDoctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");

        logger.info("更新医生信息...");
        mainDP = new DoctorProfile(true);
        UpdateDoctorProfile.updateDoctorProfile(mainToken, mainDP);
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        JSONObject doctor = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor");
        mainDoctorName = doctor.getString("name");
        mainDoctorHospitalId = doctor.getString("hospital_id");
        mainDoctorHospitalName = doctor.getString("hospital_name");

        logger.info("mainDoctorId为:\t"+mainDoctorId);
        logger.info("mainDoctorName为:\t"+mainDoctorName);
        logger.info("mainDoctorHospitalId为:\t"+mainDoctorHospitalId);
        logger.info("mainDoctorHospitalName为:\t"+mainDoctorHospitalName);
        HashMap<String, String> tmp = RegisteredDoctor_Certify_V2.certify(mainDoctorId, "1");
        if (tmp.get("is_verified").equals("1")) {
            logger.info("认证成功");
        } else {
            logger.error("医生认证没通过，无法进行后续用例，退出...");
            System.exit(1001);
        }
        mainExpertId = tmp.get("register_id");
        mainOperatorId = "chao.fang@mingyizhudao.com";
        mainOperatorName = "方超（男）";
        mainOperatorRole = "";


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
        UpdateDoctorProfile.updateDoctorProfile(mainToken, mainDP);
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

    public static String parseJson(JSONObject node, String path) {

        if (node == null) return null;
        if (!path.contains(":")) {
            if ( path.indexOf("(")+1 == path.indexOf(")") ) { // 不指定数组坐标
                if (node.getJSONArray(path.substring(0,path.length()-2)).size() >= 0) { //jsonArray不为空
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.length() - 2)).size());
                    return String.valueOf(node.getJSONArray(path.substring(0,path.length()-2)).size()); //返回数组长度
                } else {
                    return null; //指定的数组key不存在
                }
            } else if ( path.indexOf("(")+1 < path.indexOf(")") ) { // 指定数组坐标
                if (node.getJSONArray(path.substring(0,path.indexOf("("))).size() > 0) {
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.indexOf("("))));
                    return node.getJSONArray(path.substring(0, path.indexOf("("))).getString(Integer.parseInt(path.substring(path.indexOf("(") + 1, path.indexOf(")")))); //返回指定坐标的内容
                } else {
                    return null; // 指定的数组key不存在，或者长度为0
                }
            } else { // 不是数组
                if (node.containsKey(path)) {
                    return node.getString(path); // 返回值,包括""
                } else {
                    return null; // key不存在
                }
            }
        }

        String nextPath = path.substring(path.indexOf(":")+1);
        String head = path.substring(0,path.indexOf(":"));
        if ( head.indexOf("(")+1 == head.indexOf(")") ) {
            if (node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0)
                return parseJson(node.getJSONArray(head.substring(0,head.length()-2)).getJSONObject(0),nextPath);
            else
                return null;
        } else if ( head.indexOf("(")+1 < head.indexOf(")") ) {
            if ( node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0 )
                return parseJson(node.getJSONArray(head.substring(0,path.indexOf("("))).getJSONObject(Integer.parseInt(head.substring(head.indexOf("(")+1,head.indexOf(")")))),nextPath);
            else
                return null;
        } else {
            if (node.containsKey(head)) {
                return parseJson(node.getJSONObject(head), nextPath);
            } else {
                return null;
            }
        }
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
    public static String CreateRegistered() {
        logger.info("创建注册用户...");
        String mobile = SendVerifyCode.send();
        String token = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(token);
        String doctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");

        logger.info("mobile为:\t"+mobile);
        logger.info("doctorId为:\t"+doctorId);

        return doctorId.isEmpty() ? null : doctorId;
    }

    //创建一个医生，并且完善信息
    public static HashMap<String, String> CreateRegisteredDoctor(DoctorProfile dp) {
        logger.info("创建医生...");
        HashMap<String,String> info = new HashMap<>();
        String mobile = SendVerifyCode.send();
        String token = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(token);
        String doctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        if (doctorId.isEmpty()) return null;
        info.put("id", doctorId);
        info.put("mobile", mobile);
        info.put("token", token);
        logger.info("更新医生信息...");
        UpdateDoctorProfile.updateDoctorProfile(token, dp);
        res = GetDoctorProfile.getDoctorProfile(token);
        String doctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
        String doctorHospitalName = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_name");

        logger.info("mobile为:\t"+mobile);
        logger.info("doctorId为:\t"+doctorId);
        logger.info("doctorHospitalId为:\t"+doctorHospitalId);
        logger.info("doctorHospitalName为:\t"+doctorHospitalName);

        if (token.isEmpty() || doctorHospitalId.isEmpty() || doctorHospitalName.isEmpty()) {
            logger.error("更新失败，医生信息不完整");
            return null;
        }
        return info;
    }

// 创建一个医生并且认证他
    public HashMap<String, String> CreateVerifiedDoctor(DoctorProfile dp) {
        logger.info("创建医生...");
        HashMap<String,String> info = new HashMap<>();
        String mobile = SendVerifyCode.send();
        String token = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(token);
        String doctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        if (doctorId.isEmpty()) return null;
        info.put("id", doctorId);
        info.put("mobile", mobile);
        info.put("token", token);
        logger.info("更新医生信息...");
//        DoctorProfile dp = new DoctorProfile(true);
        UpdateDoctorProfile.updateDoctorProfile(token, dp);
        res = GetDoctorProfile.getDoctorProfile(token);
        String doctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
        String doctorHospitalName = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_name");

        logger.info("mobile为:\t"+mobile);
        logger.info("doctorId为:\t"+doctorId);
        logger.info("doctorHospitalId为:\t"+doctorHospitalId);
        logger.info("doctorHospitalName为:\t"+doctorHospitalName);

        logger.info("认证医生信息...");
        HashMap<String, String> tmp = RegisteredDoctor_Certify_V2.certify(doctorId, "1");
        String is_verified = tmp.get("is_verified");
        String expertId = tmp.get("register_id");
        if (!is_verified.equals("1")) {
            logger.error("认证失败");
        }
        info.put("is_verified", is_verified);
        info.put("expert_id", expertId);
        return info;
    }

}
