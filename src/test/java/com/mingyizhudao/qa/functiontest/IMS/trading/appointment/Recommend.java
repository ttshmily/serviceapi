package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;

import java.util.List;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Recommend extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/surgeons";

    public static boolean s_Recommend(String orderNumber, List<String> a) {
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject body = new JSONObject();
        body.put("list", a.toString());
        String res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        //TODO
        return r.getJSONObject("data").getString("status").equals("COMPLETED") &&
                r.getJSONObject("data").getString("status").equals("9000");
    }
}
