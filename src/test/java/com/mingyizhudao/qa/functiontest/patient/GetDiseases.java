package com.mingyizhudao.qa.functiontest.patient;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by TianJing on 2017/9/1.
 */
public class GetDiseases extends BaseTest{

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/appointment/api/v1/diseases";

    @Test
    public void test_01_获取常见病列表(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "20");
        query.put("isCommon", "1");
        query.put("name", "");
        System.out.println(query);
        res = HttpRequest.s_SendGet(host_patient + uri, query, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"), "疾病列表为空");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):id"), "", "疾病ID为空");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):name"), "", "疾病名称为空");
        Assert.assertNotNull(Helper.s_ParseJson(data, "size"), "列表总量字段不存在");
        Assert.assertEquals(Helper.s_ParseJson(data, "page_size"), "20", "默认分页大小不为20");
        Assert.assertNotNull(Helper.s_ParseJson(data, "page"), "默认没有传回第1页");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "20", "分页的默认值不为20");

        query.put("page","2");
        res = HttpRequest.s_SendGet(host_patient + uri, query, "", null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    @Test
    public void test_02_搜索疾病后返回疾病列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        for (String diseaseName: new String[] {"测试", "ceshi", "测shi", "疾病"}) {
            logger.info("搜索字段：" + diseaseName);
            query.put("name", diseaseName);
            query.put("page", "1");
            query.put("pageSize", "20");
            query.put("isCommon", "1");

            res = HttpRequest.s_SendGet(host_patient + uri, query, "");
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            JSONArray disease_list = data.getJSONArray("list");
            Assert.assertNotEquals(disease_list.size(), 0);
            Assert.assertNotNull(disease_list.getJSONObject(0).getString("name"), "疾病的name字段缺失");
            Assert.assertNotNull(disease_list.getJSONObject(0).getString("id"), "疾病的id字段缺失");
        }
    }

}
