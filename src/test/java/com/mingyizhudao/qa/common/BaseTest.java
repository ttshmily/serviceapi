package com.mingyizhudao.qa.common;


import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Certify;
import com.mingyizhudao.qa.testcase.doctor.*;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.SendVerifyCode;
import com.mingyizhudao.qa.util.HttpRequest;
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
    public static String crm_token = "";

    public static String mainMobile = "";
    public static String mainToken = "";
    public static String mainDoctorId = "";
    public static String mainDoctorName = "";
    public static String mainDoctorHospitalId = "";
    public static String mainDoctorHospitalName = "";

    public static String mainOperatorId = "";
    public static String mainOperatorName = "";
    public static String mainOperatorRole = "";

    public String code = "";
    public String message = "";
    public JSONObject data;

    public BaseTest() {

    }

    static  {
        PropertyConfigurator.configure(BaseTest.class.getClassLoader().getResource("log4j.properties"));

        // 读取配置文件
        Properties prop = new Properties();
        try {
            InputStream in = BaseTest.class.getClassLoader().getResourceAsStream("environment.properties");
//                    new BufferedInputStream(new FileInputStream("src/test/resources/config-test/environment.properties"));
            prop.load(in);
            in.close();
        } catch (IOException e) {
            logger.error(e);
            logger.error("初始化配置失败，退出...");
            System.exit(1);
        }

        {
            // 初始化配置文件中的变量
            BaseTest.protocol = prop.getProperty("protocol");
            BaseTest.host_doc = prop.getProperty("host_doc");
            BaseTest.host_crm = prop.getProperty("host_crm");
            BaseTest.host_login = prop.getProperty("host_login");
            BaseTest.host_kb = prop.getProperty("host_kb");
            BaseTest.crm_token = prop.getProperty("crm_token");

            BaseTest.host_doc = protocol.concat("://").concat(host_doc);
            BaseTest.host_crm = protocol.concat("://").concat(host_crm);
            BaseTest.host_login = protocol.concat("://").concat(host_login);
            BaseTest.host_kb = protocol.concat("://").concat(host_kb);
        }
        new Enum();
//        System.exit(1000);
    }

    @BeforeSuite
    public void setUpSuite() throws Exception {
        mainMobile = SendVerifyCode.send();
        mainToken = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(mainToken);
//        logger.info(HttpRequest.unicodeString(res));
        mainDoctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");

        logger.info("更新医生信息...");
        UpdateDoctorProfile.updateDoctorProfile(mainToken, null);
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        JSONObject doctor = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor");
        mainDoctorName = doctor.getString("name");
        mainDoctorHospitalId = doctor.getString("hospital_id");
        mainDoctorHospitalName = doctor.getString("hospital_name");

        logger.info("mainDoctorId为:\t"+mainDoctorId);
        logger.info("mainDoctorName为:\t"+mainDoctorName);
        logger.info("mainDoctorHospitalId为:\t"+mainDoctorHospitalId);
        logger.info("mainDoctorHospitalName为:\t"+mainDoctorHospitalName);

//        System.exit(1);
//        logger.info("认证医生...");
//        if (CrmCertifiedDoctor.certify(JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id"))) {
//            logger.info("认证成功");
//        } else {
//            System.exit(1);
//        }

        if (RegisteredDoctor_Certify.certify(mainDoctorId).equals("1")) {
            logger.info("认证成功");
        } else {
            logger.error("医生认证没通过，无法进行后续用例，退出...");
            System.exit(1);
        }

        mainOperatorId = "";
        mainOperatorName = "";
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
        UpdateDoctorProfile.updateDoctorProfile(mainToken, null);
        logger.info("============================================================================================================= ");
        logger.info("||    TestAPI END:\t" + getClass().getSimpleName());
        logger.info("============================================================================================================= \n");

    }

    @BeforeMethod
    public void setUpTC(Method method) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n");
        logger.info("TestCase START:\t" + method.getName());
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n");
    }

    @AfterMethod
    public void tearDownTC(Method method) {
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \n");
        logger.info("TestCase END:\t" + method.getName());
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
//        if (head.equals(last_head) && nextPath.equals(last_next)) ;
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
        if (json.containsKey("data")) {
            if (json.getString("data").equals(""))
                this.data = JSONObject.fromObject("{}");
            else if (json.getString("data").equals("[]"))
                this.data = JSONObject.fromObject("{}");
            else
                this.data = json.getJSONObject("data");
        } else {
            this.data = null;
        }
        this.code = json.getString("code");
        this.message = json.getString("message");
        logger.info("<<<<<< [ code ]:\t" + code);
        logger.info("<<<<<< [ message ]:\t" + message);
        logger.info("<<<<<< [ data ]:\t" + data);
    }

    public String CreateRegisteredDoctor() {
        logger.info("创建医生...");
        String mobile = SendVerifyCode.send();
        String token = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(token);
        String doctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
        if (doctorId.isEmpty()) return null;

        logger.info("更新医生信息...");
        UpdateDoctorProfile.updateDoctorProfile(token, null);
        res = GetDoctorProfile.getDoctorProfile(token);
        String doctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
        String doctorHospitalName = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_name");

        logger.info("mobile为:\t"+mobile);
        logger.info("doctorId为:\t"+doctorId);
        logger.info("doctorHospitalId为:\t"+doctorHospitalId);
        logger.info("doctorHospitalName为:\t"+doctorHospitalName);

        if (token.isEmpty() || doctorHospitalId.isEmpty() || doctorHospitalName.isEmpty()) logger.error("更新失败，医生信息不完整");
        return doctorId;
    }

    public String CreateRegistered() {
        logger.info("创建注册用户...");
        String mobile = SendVerifyCode.send();
        String token = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(token);
        String doctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");

        logger.info("mobile为:\t"+mobile);
        logger.info("doctorId为:\t"+doctorId);

        return doctorId.isEmpty() ? null : doctorId;
    }

}
