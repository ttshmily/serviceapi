package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;

import java.util.HashMap;

public class Statistics extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/statistics";

    public void test_01_检查所有字段齐全() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertNotNull(data.getString("active_doctor_count"));
        Assert.assertNotNull(data.getString("doctor_count"));
        Assert.assertNotNull(data.getString("paid_order_count"));
        Assert.assertNotNull(data.getString("pre_order_fee_sum"));
        Assert.assertNotNull(data.getString("success_order_count"));
        Assert.assertNotNull(data.getString("surgeon_count"));
        Assert.assertNotNull(data.getString("surgeon_fee_sum"));
        Assert.assertNotNull(data.getString("surgeon_order_count"));

    }

    public void test_02_检查管理员级别的token() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("referrerId", "SH0143");
        res = HttpRequest.s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
    }

    public void test_03_检查地推个人的token() {

    }

    public void test_04_检查筛选时间() {

    }

    public void test_05_检查筛选人员() {

    }

}
