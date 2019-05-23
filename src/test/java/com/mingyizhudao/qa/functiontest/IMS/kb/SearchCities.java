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
public class SearchCities extends BaseTest{

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/cities";

    @Test
    public void test_01_查询默认城市列表_正确输入省份() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        // city in province
        logger.info("在江苏省查询城市列表");
        query.put("province_id", "320000"); // 江苏
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "没有根据省份返回城市列表");
    }

    @Test
    public void test_02_查询默认城市列表_省份为空(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        //省份为空
        logger.info("省份为空查询城市列表");
        query.put("province_id", "");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "省份为空时应不返回城市列表");
    }

    @Test
    public void test_03_查询默认城市列表_省份ID为0(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        logger.info("省份ID为0时查询城市列表");
        query.put("province_id", "0");
        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000", "省份为0时应不返回城市列表");
//        Assert.assertEquals(s_ParseJson(data,"list"),"[]","输入无效省份应返回空结果");
    }
}
