package com.mingyizhudao.qa.KbController;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by TianJing on 2017/10/23.
 */
public class SearchDiseases extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/diseases";

    @Test
    public void test_01_查询默认疾病列表_正常路径输入查询条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入疾病名查询疾病");
        query.put("name", "疾病");
        query.put("page", "1");
        query.put("pageSize", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入疾病应返回相应的搜索结果");
    }

    @Test
    public void test_02_查询疾病列表_不输入查询条件返回() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("不输入疾病名称查询疾病");
        query.put("name", null);
        query.put("page", "1");
        query.put("pageSize", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "不输入疾病时返回默认结果");
    }

    @Test
    public void test_03_查询疾病列表_输入无效关键字(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("输入无效关键字查询疾病");
        query.put("name", "王");
        query.put("page", "1");
        query.put("pageSize", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNull(s_ParseJson(data, "list"),"输入无效关键字时应返回空结果");

    }
}
