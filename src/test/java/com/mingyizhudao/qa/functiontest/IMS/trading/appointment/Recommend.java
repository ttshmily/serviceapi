package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.AppointmentTask;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static com.mingyizhudao.qa.functiontest.IMS.trading.appointment.Create.s_CreateTid;
import static com.mingyizhudao.qa.utilities.Helper.unicodeString;
import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPut;

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
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        JSONObject body = new JSONObject();
        if (a == null || a.isEmpty()) {
            return false;
        }
        body.put("list", a.toString());
        String res = s_SendPut(host_ims+uri, body.toString(), crm_token, pathValue);
        JSONObject r = JSONObject.fromObject(res);
        if (!r.getString("code").equals("1000000")) logger.error(unicodeString(res));
        return r.getString("code").equals("1000000");
    }

    public static boolean s_Recommend(String orderNumber) {
        List<String> a = new ArrayList<>();
        a.add(Generator.randomExpertId());
        a.add(Generator.randomExpertId());
        return s_Recommend(orderNumber, a);
    }

    @Test
    public void test_01_推荐一名或多名医生() {
        String res = "";
        String tid = s_CreateTid(new AppointmentTask());
        String orderNumber = getOrderNumberByTid(tid);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        List<String> list = new ArrayList<>();
        int size = (int)Generator.randomInt(10);
        for (int i = 0; i < size; i++) {
            list.add(Generator.randomExpertId());
        }
        body.put("list", list.toString());

        res = s_SendPut(host_ims + uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray recommend_doctors = data.getJSONArray("recommend_doctors");

        int real_size = new ArrayList(new HashSet(list)).size();
        List<String> real_list = new ArrayList<>();
        Assert.assertEquals(recommend_doctors.size(), real_size);
        for (int i = 0; i < real_size; i++) {
            JSONObject doctor = recommend_doctors.getJSONObject(i);
            String id = doctor.getString("id");
            Assert.assertTrue(list.contains(id));
            real_list.add(id);
            Assert.assertEquals(doctor.getString("name"), Generator.expertName(id));
            String hospital_id = getHospitalIdByExpertId(id);
            Assert.assertEquals(doctor.getString("hospital_id"), hospital_id);
            Assert.assertEquals(doctor.getString("hospital_name"), Generator.hospitalName(hospital_id));
        }

        for (int i = 0; i < list.size(); i++) {
            Assert.assertTrue(real_list.contains(list.get(i)));
        }

    }

    @Test
    public void test_02_推荐医生_检查工单记录() {
        String res = "";
        String tid = s_CreateTid(new AppointmentTask());
        String orderNumber = getOrderNumberByTid(tid);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        List<String> list = new ArrayList<>();
        int size = (int)Generator.randomInt(10);
        for (int i = 0; i < size; i++) {
            list.add(Generator.randomExpertId());
        }
        body.put("list", list.toString());

        res = s_SendPut(host_ims + uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        logger.info("检查工单记录");
        res = Detail.s_Detail(tid);
        s_CheckResponse(res);

        JSONArray track_list = data.getJSONArray("track_list");
        int track_size = track_list.size();
        JSONObject track = track_list.getJSONObject(track_size-1);
        Assert.assertEquals(track.getString("track_type"), "EDIT_MEDICAL_ADVICE_V1");
        Assert.assertEquals(track.getString("poster_name"), mainOperatorName);
    }

    @Test
    public void test_03_推荐列表中有错误的ID() {
        String res = "";
        String tid = s_CreateTid(new AppointmentTask());
        String orderNumber = getOrderNumberByTid(tid);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        List<String> list = new ArrayList<>();
        int size = (int)Generator.randomInt(10);
        for (int i = 0; i < size; i++) {
            list.add(Generator.randomExpertId());
        }
        List<String> no_repeat_list = new ArrayList<>(new HashSet<>(list));
        no_repeat_list.add("111111111");
        body.put("list", no_repeat_list.toString());

        res = s_SendPut(host_ims + uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray recommend_doctors = data.getJSONArray("recommend_doctors");

        int real_size = no_repeat_list.size() - 1; //减去一个错误ID

        Assert.assertEquals(recommend_doctors.size(), real_size);
    }

    @Test
    public void test_04_推荐列表中有重复的ID() {
        String res = "";
        String tid = s_CreateTid(new AppointmentTask());
        String orderNumber = getOrderNumberByTid(tid);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);

        JSONObject body = new JSONObject();
        List<String> list = new ArrayList<>();
        int size = (int)Generator.randomInt(10);
        for (int i = 0; i < size; i++) {
            list.add(Generator.randomExpertId());
        }
        list.add(list.get(list.size()-1));
        body.put("list", list.toString());

        res = s_SendPut(host_ims + uri, body.toString(), crm_token, pathValue);

        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        res = Detail.s_Detail(tid);
        s_CheckResponse(res);
        JSONArray recommend_doctors = data.getJSONArray("recommend_doctors");

        int real_size = new ArrayList(new HashSet(list)).size();
        List<String> real_list = new ArrayList<>();
        Assert.assertEquals(recommend_doctors.size(), real_size);
        for (int i = 0; i < real_size; i++) {
            JSONObject doctor = recommend_doctors.getJSONObject(i);
            String id = doctor.getString("id");
            Assert.assertTrue(list.contains(id));
            real_list.add(id);
            Assert.assertEquals(doctor.getString("name"), Generator.expertName(id));
        }

        for (int i = 0; i < list.size(); i++) {
            Assert.assertTrue(real_list.contains(list.get(i)));
        }
    }

    private String getOrderNumberByTid(String tid) {
        return JSONObject.fromObject(Detail.s_Detail(tid)).getJSONObject("data").getJSONObject("appointment_order").getString("order_number");
    }

    private String getHospitalIdByExpertId(String expert_id) {
        return JSONObject.fromObject(KBExpert_Detail.s_Detail(expert_id)).getJSONObject("data").getString("hospital_id");
    }
}
