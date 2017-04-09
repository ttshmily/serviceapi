package com.mingyizhudao.qa.common;


import com.mingyizhudao.qa.tc.*;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ttshmily on 20/3/2017.
 */
public class BaseTest {

    public static final Logger logger= Logger.getLogger(BaseTest.class);
    public static String host = "";
    public static String protocol = "";
    public static String mainMobile = "";
    public static String mainToken = "";

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
            protocol = prop.getProperty("protocol");
            host = prop.getProperty("host");
            SendVerifyCode.host = prop.getProperty("SendVerifyCode.host");
            SendVerifyCode.uri = prop.getProperty("SendVerifyCode.uri");
            CheckVerifyCode.host = prop.getProperty("CheckVerifyCode.host");
            CheckVerifyCode.uri = prop.getProperty("CheckVerifyCode.uri");
            Refresh.host = prop.getProperty("Refresh.host");
            Refresh.uri = prop.getProperty("Refresh.uri");
            CrmCertifiedDoctor.uri = prop.getProperty("CrmCertifiedDoctor.uri");
            host = protocol.concat(host);
            SendVerifyCode.host = protocol.concat(SendVerifyCode.host);
            CheckVerifyCode.host = protocol.concat(CheckVerifyCode.host);
            Refresh.host = protocol.concat(Refresh.host);
            logger.debug(host);
            logger.debug(SendVerifyCode.host);
            logger.debug(CheckVerifyCode.host);
            logger.debug(Refresh.host);
        }
        mainMobile = SendVerifyCode.send();
        mainToken = CheckVerifyCode.check();
        String res = GetDoctorProfile.getDoctorProfile(mainToken);
        logger.info("mainDoctorId为"+JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id"));
        HashMap<String, String> profile = new HashMap<String, String>();
        UpdateDoctorProfile.updateDoctorProfile(mainToken, profile);
        CrmCertifiedDoctor.certify(JSONObject.fromObject(res).getJSONObject("data").getJSONObject("doctor").getString("user_id"));
//        System.exit(0);
    }

    @BeforeClass
    public void setUpClass() throws Exception {

        logger.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////// ");
        logger.info("//    TestAPI START:\t" + getClass().getSimpleName());
        logger.info("///////////////////////////////////////////////////////////////////////////////////////////////////////////// \n");

    }

    @AfterClass
    public void tearDownClass() throws Exception {

        logger.info("============================================================================================================= ");
        logger.info("||    TestAPI END:\t" + getClass().getSimpleName());
        logger.info("============================================================================================================= \n");

    }

    @BeforeMethod
    public void setUpTC(Method method) throws Exception {

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        logger.info("TestCase START:\t" + method.getName());
    }

    @AfterMethod
    public void tearDownTC(Method method) {
        logger.info("TestCase END:\t" + method.getName());
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< \n");
    }

    public static String unicodeString( String strArr )
    {
        List<String> list	= new ArrayList<String>();
        String		zz	= "\\\\u[0-9,a-z,A-Z]{4}";
		/* 正则表达式用法参考API */
        Pattern pattern = Pattern.compile( zz );
        Matcher m	= pattern.matcher( strArr );
        while ( m.find() )
        {
            list.add( m.group() );
        }
        for ( int i = 0, j = 2; i < list.size(); i++ )
        {
            String st = list.get( i ).substring( j, j + 4 );
			/* 将得到的数�?�按�?16进制解析为十进制整数，再強转为字�? */
            char ch = (char) Integer.parseInt( st, 16 );
			/* 用得到的字符替换编码表达�? */
            strArr = strArr.replace( list.get( i ), String.valueOf( ch ) );
        }
        return(strArr);
    }

    public static String parseJson(JSONObject node, String path) {
        if (!path.contains(":")) {
            if ( path.indexOf("(")+1 == path.indexOf(")") ) {
                if (node.getJSONArray(path.substring(0,path.length()-2)).size() > 0) { //jsonArray不为空
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.length() - 2)).size());
                    return node.getJSONArray(path.substring(0, path.length() - 2)).getString(0);
                } else {
                    return null;
                }
            } else if ( path.indexOf("(")+1 < path.indexOf(")") ) {
                //DONE 3是有问题的，需要根据实际的index长度相应变化，暂留bug
                if (node.getJSONArray(path.substring(0,path.indexOf("("))).size() > 0) {
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.indexOf("("))));
                    return node.getJSONArray(path.substring(0, path.indexOf("("))).getString(Integer.parseInt(path.substring(path.indexOf("(") + 1, path.indexOf(")"))));
                } else {
                    return null;
                }
            } else {
                if (node.containsKey(path)) {
                    return node.getString(path);
                } else {
                    return null;
                }
            }
        }

        String nextPath = path.substring(path.indexOf(":")+1);
        String head = path.substring(0,path.indexOf(":"));
//        logger.debug(nextPath);
//        logger.debug(head);
        if ( head.indexOf("(")+1 == head.indexOf(")") ) {
            if (node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0)
                return parseJson(node.getJSONArray(head.substring(0,head.length()-2)).getJSONObject(0),nextPath);
            else
                return null;
        } else if ( head.indexOf("(")+1 < head.indexOf(")") ) {
            //DONE 3是有问题的，需要根据实际的index长度相应变化，暂留bug
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
        this.data = json.getJSONObject("data");
        this.code = json.getString("code");
        this.message = json.getString("message");
        logger.info("<<<<<< [ code ]:\t" + code);
        logger.info("<<<<<< [ message ]:\t" + message);
        logger.info("<<<<<< [ data ]:\t" + data);
    }

}
