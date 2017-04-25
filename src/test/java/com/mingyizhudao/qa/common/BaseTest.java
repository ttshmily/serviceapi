package com.mingyizhudao.qa.common;


import com.mingyizhudao.qa.testcase.*;
import com.mingyizhudao.qa.testcase.doctor.*;
import com.mingyizhudao.qa.testcase.login.CheckVerifyCode;
import com.mingyizhudao.qa.testcase.login.Refresh;
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
    public static String mainMobile = "";
    public static String mainToken = "";
    public static String mainDoctorId = "";
    public static String mainDoctorHospitalId = "";
    public static String mainDoctorHospitalName = "";

    public String code = "";
    public String message = "";
    public JSONObject data;

    public BaseTest() {
//        PropertyConfigurator.configure("resources/log4j.properties");
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
            System.exit(1);
        }

        {
            // 初始化配置文件中的变量
            for (String key:prop.stringPropertyNames()
                 ) {
                key = prop.getProperty(key);
            }
            BaseTest.protocol = prop.getProperty("protocol");

            BaseTest.host_doc = prop.getProperty("host_doc");
            BaseTest.host_crm = prop.getProperty("host_crm");
            BaseTest.host_login = prop.getProperty("host_login");
//            SendVerifyCode.host_login = prop.getProperty("SendVerifyCode.host");
//            SendVerifyCode.uri = prop.getProperty("SendVerifyCode.uri");
//            CheckVerifyCode.host_login = prop.getProperty("CheckVerifyCode.host");
//            CheckVerifyCode.uri = prop.getProperty("CheckVerifyCode.uri");
//            Refresh.host_login = prop.getProperty("Refresh.host");
//            Refresh.uri = prop.getProperty("Refresh.uri");

//            CrmCertifiedDoctor.uri = prop.getProperty("CrmCertifiedDoctor.uri");
//            GetDoctorProfile.uri = prop.getProperty("GetDoctorProfile.uri");
//            UpdateDoctorProfile.uri = prop.getProperty("UpdateDoctorProfile.uri");
//            HospitalSearch.uri = prop.getProperty("HospitalSearch.uri");

            BaseTest.host_doc = protocol.concat("://").concat(host_doc);
            BaseTest.host_crm = protocol.concat("://").concat(host_crm);
            BaseTest.host_login = protocol.concat("://").concat(host_login);
//            SendVerifyCode.host_login = protocol.concat(SendVerifyCode.host_login);
//            CheckVerifyCode.host_login = protocol.concat(CheckVerifyCode.host_login);
//            Refresh.host_login = protocol.concat(Refresh.host_login);
//            logger.debug(host_doc);
//            logger.debug(SendVerifyCode.host_doc);
//            logger.debug(CheckVerifyCode.host_doc);
//            logger.debug(Refresh.host_doc);
        }

//
//        mainMobile = SendVerifyCode.send();
//        mainToken = CheckVerifyCode.check();
//
//        String res = GetDoctorProfile.getDoctorProfile(mainToken);
//        mainDoctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");
//
//        logger.info("更新医生信息...");
//        UpdateDoctorProfile.updateDoctorProfile(mainToken, null);
//        res = GetDoctorProfile.getDoctorProfile(mainToken);
//        mainDoctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
//        mainDoctorHospitalName = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_name");
//        logger.info("mainDoctorId为:\t"+mainDoctorId);
//        logger.info("mainDoctorHospitalId为:\t"+mainDoctorHospitalId);
//        logger.info("mainDoctorHospitalName为:\t"+mainDoctorHospitalName);
//
//        logger.info("认证医生...");
//        if (CrmCertifiedDoctor.certify(JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id")))
//            logger.info("认证成功");
//        else
//            System.exit(1);
    }

    @BeforeSuite
    public void setUpSuite() throws Exception {
        mainMobile = SendVerifyCode.send();
        mainToken = CheckVerifyCode.check();

        String res = GetDoctorProfile.getDoctorProfile(mainToken);
        mainDoctorId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id");

        logger.info("更新医生信息...");
        UpdateDoctorProfile.updateDoctorProfile(mainToken, null);
        res = GetDoctorProfile.getDoctorProfile(mainToken);
        mainDoctorHospitalId = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_id");
        mainDoctorHospitalName = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("hospital_name");

        logger.info("mainDoctorId为:\t"+mainDoctorId);
        logger.info("mainDoctorHospitalId为:\t"+mainDoctorHospitalId);
        logger.info("mainDoctorHospitalName为:\t"+mainDoctorHospitalName);

        logger.info("认证医生...");
        if (CrmCertifiedDoctor.certify(JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id"))) {
            logger.info("认证成功");
            System.exit(1);
        } else
            System.exit(1);

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
                    return null; //指定的数组不存在
                }
            } else if ( path.indexOf("(")+1 < path.indexOf(")") ) { // 指定数组坐标
                if (node.getJSONArray(path.substring(0,path.indexOf("("))).size() > 0) {
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.indexOf("("))));
                    return node.getJSONArray(path.substring(0, path.indexOf("("))).getString(Integer.parseInt(path.substring(path.indexOf("(") + 1, path.indexOf(")")))); //返回指定坐标的内容
                } else {
                    return null;
                }
            } else { // 不是数组
                if (node.containsKey(path)) {
                    return node.getString(path); // 返回值
                } else {
                    return null;
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
        this.data = json.getJSONObject("data").equals("null") ? null : json.getJSONObject("data");
        this.code = json.getString("code");
        this.message = json.getString("message");
        logger.info("<<<<<< [ code ]:\t" + code);
        logger.info("<<<<<< [ message ]:\t" + message);
        logger.info("<<<<<< [ data ]:\t" + data);
    }

}
