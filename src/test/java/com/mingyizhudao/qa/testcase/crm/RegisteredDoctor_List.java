package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_List extends BaseTest{

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_List.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取医生列表_使用默认值() {

        String res = "";
        HashMap<String, String> pathValue = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, "", mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_获取医生列表_传入特定的页码和分页大小() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page","2");
        query.put("page_size", "100");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_03_获取医生列表_传入特定的医生姓名搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        //以姓名进行搜索
        query.put("doctor_name","大一");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        //更换搜索的姓名，确认搜索结果正确性
        query.replace("doctor_name","大二");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_04_获取医生列表_传入特定的医生手机搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("mobile","13300000001");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("mobile","13300000002");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_05_获取医生列表_传入特定的地推姓名搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent","苏舒");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("agent","谢瑾");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_06_获取医生列表_传入特定的地推手机搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent_mobile","13811112222");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("agent_mobile","138111122222");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_07_获取医生列表_传入特定的医生姓名和认证状态() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("doctor_name","大一");
        query.put("certified_status","2");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("certified_status","1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("certified_status","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_08_获取医生列表_传入特定的地推姓名和认证状态() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent_name","苏舒");
        query.put("certified_status","2");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("certified_status","1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("certified_status","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_09_获取医生列表_传入特定的地推姓名和学术职称() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent_name","苏舒");
        query.put("medical_title","2");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("medical_title","1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("medical_title","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, mainToken, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

}
