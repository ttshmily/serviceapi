package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import net.sf.json.JSONObject;
import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import static com.mingyizhudao.qa.utilities.Helper.*;

public class Ap_Rollback extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}/cleanSurgeons";

    public static boolean s_Rollback(String orderNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        body.put("content", "客服记录内容");
        body.put("reason", "官方选择理由");
        String res = s_SendPut(host_appointment + uri, "", crm_token, pathValue);
        String status = JSONObject.fromObject(res).getJSONObject("data").getString("status");
        return status.equals("1000") ? true : false;
    }

    public void test_01_回退面诊订单() {

    }

    public void test_02_回退面诊订单理由不完整() {

    }

}
