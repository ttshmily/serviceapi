package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendGet;

public class GetBdList extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/getBdAssistants";

    private int inServiceCount=0;
    private int outServiceCount=0;

    @Test
    public void test_01_获取在职人员() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("inService", "true");

        res = s_SendGet(host_bda+uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        JSONArray staff_list = data.getJSONArray("list");
        for (int i = 0; i < staff_list.size() ; i++) {
            JSONObject staff = staff_list.getJSONObject(i);
            Assert.assertNotNull(staff.getString("avatar"));
            Assert.assertNotNull(staff.getString("count"));
            Assert.assertNotNull(staff.getString("doctor_counts"));
            Assert.assertNotNull(staff.getString("name"));
            Assert.assertNotNull(staff.getString("referrer_id"));
        }
        inServiceCount = staff_list.size();
    }

    @Test
    public void test_02_获取离职人员列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("inService", "false");

        res = s_SendGet(host_bda+uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        JSONArray staff_list = data.getJSONArray("list");
        for (int i = 0; i < staff_list.size() ; i++) {
            JSONObject staff = staff_list.getJSONObject(i);
            Assert.assertNotNull(staff.getString("avatar"));
            Assert.assertNotNull(staff.getString("dimission_date"));
            Assert.assertNotNull(staff.getString("count"));
            Assert.assertNotNull(staff.getString("doctor_counts"));
            Assert.assertNotNull(staff.getString("name"));
            Assert.assertNotNull(staff.getString("referrer_id"));
        }
        outServiceCount = staff_list.size();
    }

    @Test
    public void test_03_获取所有人员列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
//        query.put("inService", "false");

        res = s_SendGet(host_bda+uri, query, bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        JSONArray staff_list = data.getJSONArray("list");
        for (int i = 0; i < staff_list.size() ; i++) {
            JSONObject staff = staff_list.getJSONObject(i);
            Assert.assertNotNull(staff.getString("avatar"));
            Assert.assertNotNull(staff.getString("count"));
            Assert.assertNotNull(staff.getString("doctor_counts"));
            Assert.assertNotNull(staff.getString("name"));
            Assert.assertNotNull(staff.getString("referrer_id"));
        }
        Assert.assertEquals(staff_list.size(), inServiceCount+outServiceCount);
    }
}
