package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Update extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}";

    public static boolean s_Update(String orderNumber, AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String res = HttpRequest.s_SendPut(host_ims+uri, at.transform(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getJSONObject("code").equals("1000000");
    }

    public static boolean s_Update(String orderNumber, JSONObject body) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        String res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getJSONObject("code").equals("1000000");
    }
}
