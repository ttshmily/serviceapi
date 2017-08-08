package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;

import static com.mingyizhudao.qa.utilities.Helper.*;

import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.net.ntp.TimeStamp;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class Ap_List extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments";

    @Test
    public void test_01_获取列表检查必要字段() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.s_SendGet(host_appointment + uri, "", crm_token);
            logger.debug("返回结果:\t"+res);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "list()"), "10");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotNull(s_ParseJson(data, "size"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):created_at"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):order_number"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):status"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):source_type"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):patient_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):patient_phone"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):modified_at"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):major_reps_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):modified_at"));
    }

    @Test
    public void test_02_获取列表检查排序创建时间() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("sortCriteria", "0");
        query.put("collatingSequence", "0");
        try {
            res = HttpRequest.s_SendGet(host_appointment + uri, query, crm_token);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray ap_list = data.getJSONArray("list");
        long tmp = Long.MAX_VALUE;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeStamp ts = null;
        for (int i=0; i < ap_list.size(); i++) {
            JSONObject ap = ap_list.getJSONObject(i);
            String date = ap.getString("created_at");
            logger.info("created_at = " + date);
            try {
                long d = new TimeStamp(df.parse(date)).getTime();
                logger.info("created_at.timeStamp = " + d);
                Assert.assertTrue(tmp >=  d);
                tmp = d;
            } catch (Exception e) {
                logger.error(e);
            }
        }

        query.put("collatingSequence", "1");
        try {
            res = HttpRequest.s_SendGet(host_appointment + uri, query, crm_token);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        ap_list = data.getJSONArray("list");
        tmp = 0;
        for (int i=0; i < ap_list.size(); i++) {
            JSONObject ap = ap_list.getJSONObject(i);
            String date = ap.getString("created_at");
            logger.info("created_at = " + date);
            try {
                long d = new TimeStamp(df.parse(date)).getTime();
                logger.info("created_at.timeStamp = " + d);
                Assert.assertTrue(tmp <= d);
                tmp = d;
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    @Test
    public void test_03_获取列表检查排序跟进时间() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("sortCriteria", "1");
        query.put("collatingSequence", "0");
        try {
            res = HttpRequest.s_SendGet(host_appointment + uri, query, crm_token);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        JSONArray ap_list = data.getJSONArray("list");
        long tmp = Long.MAX_VALUE;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        TimeStamp ts = null;
        for (int i=0; i < ap_list.size(); i++) {
            JSONObject ap = ap_list.getJSONObject(i);
            String date = ap.getString("modified_at");
            logger.info("modified_at = " + date);
            try {
                long d = new TimeStamp(df.parse(date)).getTime();
                logger.info("modified_at.timeStamp = " + d);
                Assert.assertTrue(tmp >=  d);
                tmp = d;
            } catch (Exception e) {
                logger.error(e);
            }
        }

        query.put("collatingSequence", "1");
        try {
            res = HttpRequest.s_SendGet(host_appointment + uri, query, crm_token);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        ap_list = data.getJSONArray("list");
        tmp = 0;
        for (int i=0; i < ap_list.size(); i++) {
            JSONObject ap = ap_list.getJSONObject(i);
            String date = ap.getString("modified_at");
            logger.info("modified_at = " + date);
            try {
                long d = new TimeStamp(df.parse(date)).getTime();
                logger.info("modified_at.timeStamp = " + d);
                Assert.assertTrue(tmp <= d);
                tmp = d;
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    @Test
    public void test_04_获取列表检查筛选状态() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", String.valueOf(Generator.randomInt(2)));
        int[] size_per_status = new int[]{};
        int i = 0;
        for (String status:new String[]{"2000","3000","3000,4000","2000,3000","4000"}) {
            logger.info("筛选状态为"+status+"的订单");
            query.put("status", status);
            List<String> status_list = Arrays.asList(status.split(","));
            res = HttpRequest.s_SendGet(host_appointment + uri, query, crm_token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            JSONArray ap_list = data.getJSONArray("list");
            for (int j=0; j < ap_list.size(); j++) {
                JSONObject ap = ap_list.getJSONObject(j);
                Assert.assertTrue(status_list.contains(ap.getString("status")));
            }
            size_per_status[i++] = data.getInt("size"); // 对应{"2000","3000","3000,4000","2000,3000","4000"}
        }
        Assert.assertTrue(size_per_status[0]+size_per_status[1]==size_per_status[3]);
        Assert.assertTrue(size_per_status[1]+size_per_status[4]==size_per_status[2]);
    }

    public void test_05_获取列表检查筛选日期() {

    }

    public void test_06_获取列表搜索患者姓名() {

    }

    public void test_07_获取列表检查搜索订单号() {

    }

}
