package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.annotations.Test;

import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Cancel extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{id}/cancel";

    public static boolean s_Cancel(AppointmentTask at) {
        TestLogger logger = new TestLogger(s_JobName());
        String res = HttpRequest.s_SendPost(host_ims+uri, at.transform(), crm_token);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getJSONObject("data").getString("status").equals("COMPLETED") &&
                r.getJSONObject("data").getString("status").equals("9000");
    }

    @Test
    public void test_01_取消订单_受理中() {

    }

    @Test
    public void test_02_取消订单_服务中() {

    }
}
