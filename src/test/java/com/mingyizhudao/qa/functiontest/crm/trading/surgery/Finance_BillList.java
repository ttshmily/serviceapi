package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by dayi on 2017/7/3.
 */
public class Finance_BillList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/finances/reconciliations";

    @Test
    public void test_01_获取支付订单列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "size"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "page_size"));
    }

    @Test
    public void test_02_获取支付订单_分页逻辑() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "1");
        query.put("pageSize", "0");
        for (int i = 1; i < 10; i++) {
            query.replace("pageSize", String.valueOf(i));
            res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(Helper.s_ParseJson(data, "list()"), String.valueOf(i));
        }
    }
}
