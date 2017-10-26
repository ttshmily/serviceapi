package com.mingyizhudao.qa.functiontest.IMS.kb;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by TianJing on 2017/10/23.
 */
public class SearchDoctors extends BaseTest{
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors";

    @Test
    public void test_01_查询医生列表_输入医生姓名(){

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入有效医生姓名搜索医生");
        query.put("name", "徐");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入医生姓名应返回相应的医生列表");

        logger.info("输入医生姓名为空搜索医生");
        query.put("name", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入医生姓名应返回相应的医生列表");

        logger.info("输入无效医生姓名搜索医生");
        query.put("name", "11111");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效医生姓名应返回空结果");
    }

    @Test
    public void test_02_查询医生列表_输入医生所在医院(){

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入有效医生医院名称搜索医生");
        query.put("hospital_name", "上海市胸科医院");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入医生医院名称应返回相应的医生列表");

        logger.info("输入医生医院名称为空搜索医生");
        query.put("hospital_name", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入医生医院名称为空应返回所有的医生列表");

        logger.info("输入无效医生医院名称搜索医生");
        query.put("hospital_name", "1111111");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效医生医院名称应返回空结果");
    }

    @Test
    public void test_03_查询医生列表_输入疾病名(){

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入有效疾病名");
        query.put("disease_name", "垂体腺瘤");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入疾病名称应返回相应的医生列表");

        logger.info("输入疾病名为空");
        query.put("disease_name", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入疾病名称为空应返回所有的医生列表");

        logger.info("输入无效疾病名");
        query.put("disease_name", "222222");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效疾病名称应返回空结果");
    }

    @Test
    public void test_04_查询医生列表_输入一级分类(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入有效的一级分类");
        query.put("parent_category_id", "17");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入一级分类应返回相应的医生列表");

        logger.info("输入一级分类为空");
        query.put("parent_category_id", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入一级分类为空应返回所有医生列表");

        logger.info("输入无效一级分类");
        query.put("parent_category_id", "0");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效一级分类名时应返回空结果");
    }

    @Test
    public void test_05_查询医生列表_输入二级分类() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入有效的二级分类");
        query.put("categoryId", "24");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入二级分类应返回相应的医生列表");

        logger.info("输入的二级分类为空");
        query.put("categoryId", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入二级分类为空应返回所有医生列表");

        logger.info("输入无效的二级分类");
        query.put("categoryId", "0");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]", "输入无效的二级分类应返回空结果");
    }

    @Test
    public void test_06_查询医生列表_输入城市(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入有效的城市名");
        query.put("city_id", "310100");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入城市名应返回相应的医生列表");

        logger.info("输入空的城市名");
        query.put("city_id", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入空城市时应返回医生列表");

        logger.info("输入无效的城市名");
        query.put("city_id", "0");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效城市名时应返回空结果");
    }
}