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

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

public class Search extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders";

    @Test
    public void test_01_按工单号搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String id = Create.s_CreateTid(new AppointmentTask());
        query.put("id", id);

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertEquals(s_ParseJson(data, "size"), "1");
        Assert.assertEquals(s_ParseJson(data, "list(0):id"), id);
        Assert.assertNotNull(s_ParseJson(data, "list(0):assignee_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):creator_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):patient_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):disease_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):created_at"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):modified_at"));
    }

    @Test
    public void test_02_按预约单号搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String id = Create.s_CreateTid(new AppointmentTask());
        query.put("order_number", getOrderNumberByTid(id));

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertEquals(s_ParseJson(data, "size"), "1");
        Assert.assertEquals(s_ParseJson(data, "list(0):id"), id);
        Assert.assertNotNull(s_ParseJson(data, "list(0):assignee_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):creator_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):patient_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):disease_name"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):created_at"));
        Assert.assertNotNull(s_ParseJson(data, "list(0):modified_at"));
    }

    @Test
    public void test_03_按患者姓名搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        AppointmentTask at = new AppointmentTask();
        String id = Create.s_CreateTid(at);
        query.put("patient_name", at.getPatient_name());

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);

        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            Assert.assertEquals(r.getString("patient_name"), at.getPatient_name());
        }
    }

    @Test
    public void test_04_按患者手机搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        AppointmentTask at = new AppointmentTask();
        String id = Create.s_CreateTid(at);
        query.put("patient_phone", at.getPatient_phone());

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            res = Detail.s_Detail(r.getString("id"));
            Assert.assertEquals(JSONObject.fromObject(res).getJSONObject("data").getJSONObject("appointment_order").getString("patient_phone"), at.getPatient_phone());
        }

    }

    @Test
    public void test_05_按工单提交人搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String id = Create.s_CreateTid(new AppointmentTask());
        String creator_id = getCreatorIdByTid(id);
        query.put("creator_id", creator_id);

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            Assert.assertEquals(r.getString("creator_name"), Generator.employeeName(creator_id));
        }
    }

    @Test
    public void test_06_按工单当前受理人搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String id = Create.s_CreateTid(new AppointmentTask());
        String assigning_id = Generator.randomEmployeeId();
        query.put("assigning_id", assigning_id);

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            String tid = r.getString("id");
            Assert.assertEquals(getAssigneeIdByTid(tid), assigning_id);
        }
    }

    public void test_07_按工单历史受理人搜索() {

    }

    @Test
    public void test_08_按提交日期搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String id = Create.s_CreateTid(new AppointmentTask());
        String date = Generator.randomDateFromNow(0, 0, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        String p = date.split("T")[0];
        query.put("created_at", date);

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            Assert.assertEquals(r.getString("created_at").split("T")[0], p);
        }

    }

    @Test
    public void test_09_按未受理搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String id = Create.s_CreateTid(new AppointmentTask());
        query.put("is_assigned", "false");

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            String tid = r.getString("id");
            Assert.assertNull(getAssigneeIdByTid(tid));
        }

    }

    @Test
    public void test_10_按受理人组搜索() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        String id = Create.s_CreateTid(new AppointmentTask());
        Assign.s_Assign(id, "SH0130"); //Group 14
        query.put("assignee_department", "14");

        res = HttpRequest.s_SendGet(host_ims + uri, query, crm_token);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "page_size"), "10");
        Assert.assertEquals(s_ParseJson(data, "page"), "1");
        Assert.assertNotEquals(s_ParseJson(data, "size"), "0");

        JSONArray result_list = data.getJSONArray("list");
        for (int i=0; i<result_list.size(); i++) {
            JSONObject r = result_list.getJSONObject(i);
            String tid = r.getString("id");
            Assert.assertEquals(getAssigneeDepartmentByTid(tid), "14");
        }
    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }

    private String getCreatorIdByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getString("creator_id");
    }

    private String getAssigneeDepartmentByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getString("assignee_department");
    }

    private String getAssigneeIdByTid(String tid) {
        String id = JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getString("assignee_id");
        return id.equals("null")?null:id;
    }
}
