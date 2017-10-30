package com.mingyizhudao.qa.functiontest.patient_edge;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;

import java.util.HashMap;

public class PaymentInfo extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/payments/{paymentNumber}/info";

    public static String s_Detail(String paymentNumber) {
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("paymentNumber", paymentNumber);
        return HttpRequest.s_SendGet(host_ims + uri, "", crm_token, pathValue);
    }
}
