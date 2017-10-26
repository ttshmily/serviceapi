package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.utilities.Generator;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.simplify;
import static com.mingyizhudao.qa.utilities.Helper.unicodeString;

public class Assign extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{id}/assign";

    public static boolean s_Assign(String tid, String assignee_id) {
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", tid);
        JSONObject body = new JSONObject();
        body.put("assignee_id", assignee_id);
        body.put("remark", "转交一下，帮忙办一下哦");
        String res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getJSONObject("data").getString("assignee_id").equals(assignee_id);
    }

    @Test
    public void test_01_创建后转交() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        String tid = Create.s_Create(at);

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", tid);
        JSONObject body = new JSONObject();
        String tmp = Generator.randomEmployeeId();
        body.put("assignee_id", tmp);
        body.put("remark", "创建完就转交");

        res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("assignee_id"), tmp);
        Assert.assertEquals(data.getString("assignee_name"), Generator.employeeName(tmp));

        JSONArray track_list = data.getJSONArray("track_list");
        Assert.assertEquals(track_list.size(), 2);
        JSONObject track = track_list.getJSONObject(track_list.size() - 1);
        Assert.assertEquals(track.getString("track_type"), "CHANGE_ASSIGNEE_V1");
        Assert.assertEquals(track.getString("poster_name"), mainOperatorName);
        Assert.assertEquals(track.getJSONObject("content").getString("assignee_id"), tmp);
        Assert.assertEquals(track.getJSONObject("content").getString("assignee_name"), Generator.employeeName(tmp));
        Assert.assertEquals(track.getJSONObject("content").getString("content"), body.getString("remark"));
        Assert.assertNotNull(track.getJSONObject("content").getString("assignee_department"));
    }

    @Test
    public void test_02_反复转交检查工单记录() {
        String res = "";
        AppointmentTask at = new AppointmentTask();
        String tid = Create.s_Create(at);

        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("id", tid);
        JSONObject body = new JSONObject();
        String tmp = Generator.randomEmployeeId();
        int times = 10;
        for (int i = 0; i < times; i++) {
            tmp = Generator.randomEmployeeId();
            logger.info("第"+i+"次转交:"+tmp);
            body.put("assignee_id", tmp);
            body.put("remark", "创建完就转交"+tmp);
            res = HttpRequest.s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
        }

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        Assert.assertEquals(data.getString("assignee_id"), tmp);
        Assert.assertEquals(data.getString("assignee_name"), Generator.employeeName(tmp));

        JSONArray track_list = data.getJSONArray("track_list");
        Assert.assertEquals(track_list.size(), times+1);
        JSONObject track = track_list.getJSONObject(track_list.size() - 1);
        Assert.assertEquals(track.getString("track_type"), "CHANGE_ASSIGNEE_V1");
        Assert.assertEquals(track.getString("poster_name"), mainOperatorName);
        Assert.assertEquals(track.getJSONObject("content").getString("assignee_id"), tmp);
        Assert.assertEquals(track.getJSONObject("content").getString("assignee_name"), Generator.employeeName(tmp));
        Assert.assertEquals(track.getJSONObject("content").getString("content"), body.getString("remark"));
        Assert.assertNotNull(track.getJSONObject("content").getString("assignee_department"));
    }
}
