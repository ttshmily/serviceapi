package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendGet;

public class VisitDate extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/visitDate";

    public static String[] s_VisitDate(String token) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = s_SendGet(host_bda + uri, "", token);
        logger.info(res);
        JSONArray date_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
        String[] a= new String[date_list.size()];
        for(int i =0; i<date_list.size(); i++) {
            a[i] = date_list.getString(i);
        }
        return a;
    }
}
