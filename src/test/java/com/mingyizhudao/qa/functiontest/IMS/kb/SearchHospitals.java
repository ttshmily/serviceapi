package com.mingyizhudao.qa.functiontest.IMS.kb;

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
public class SearchHospitals extends BaseTest{

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/hospitals";

    @Test
    public void test_01_查询医院列表_输入有效医院名称(){

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("name", "上海");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入医院名应返回相应的结果");
    }

    @Test
    public void test_02_查询医院列表_输入医院名称为空(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("name", "");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "输入医院名为空应返回所有结果");
    }

    @Test
    public void test_03_查询医院列表_输入无效医院名称(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("name", "11111");
        query.put("page", "1");
        query.put("page_size", "10");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效医院名应返回空结果");
    }
}
