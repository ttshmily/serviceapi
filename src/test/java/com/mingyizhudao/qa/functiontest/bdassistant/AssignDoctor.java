package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.functiontest.crm.kb.management.KBExpert_Detail;
import com.mingyizhudao.qa.functiontest.crm.user.management.RegisteredDoctor_Detail;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.unicodeString;
import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendPost;

public class AssignDoctor extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/assignmentDoctor";


    @Test
    public void test_01_分配一个医生到地推() {
        String res = "";
        JSONObject body = new JSONObject();
        List<Integer> doctor_list = new ArrayList<>();
        doctor_list.add(getRegIdByKbId(randomExpertId()));
        String bd_id = randomEmployeeId();
        body.put("doctor_list", doctor_list);
        body.put("staff_id", bd_id);

        res = s_SendPost(host_bda+uri, body.toString(), bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        for (int reg_id:doctor_list) {
            Assert.assertEquals(getServeIdByRegId(String.valueOf(reg_id)), bd_id);
        }
    }

    @Test
    public void test_02_分配多个医生到地推() {
        String res = "";
        JSONObject body = new JSONObject();
        List<Integer> doctor_list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            doctor_list.add(getRegIdByKbId(randomExpertId()));
        }
        String bd_id = randomEmployeeId();
        body.put("doctor_list", doctor_list);
        body.put("staff_id", bd_id);

        res = s_SendPost(host_bda+uri, body.toString(), bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        for (int reg_id:doctor_list) {
            Assert.assertEquals(getServeIdByRegId(String.valueOf(reg_id)), bd_id);
        }
    }

    @Test
    public void test_03_分配错误医生到地推() {
        String res = "";
        JSONObject body = new JSONObject();
        List<Integer> doctor_list = new ArrayList<>();
        doctor_list.add(100000);
        String bd_id = randomEmployeeId();
        body.put("doctor_list", doctor_list);
        body.put("staff_id", bd_id);

        res = s_SendPost(host_bda+uri, body.toString(), bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertNull(getServeIdByRegId(String.valueOf(doctor_list.get(0))));
    }

    @Test
    public void test_04_分配多个包含错误医生到地推() {
        String res = "";
        JSONObject body = new JSONObject();
        List<Integer> doctor_list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            doctor_list.add(getRegIdByKbId(randomExpertId()));
        }
        doctor_list.add(100000);
        String bd_id = randomEmployeeId();
        body.put("doctor_list", doctor_list);
        body.put("staff_id", bd_id);

        res = s_SendPost(host_bda+uri, body.toString(), bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(data.getJSONArray("list").size(), doctor_list.size()-1);

        for (int i = 0; i < 5; i++) { // 只有5个id是有效ID
            Assert.assertEquals(getServeIdByRegId(String.valueOf(doctor_list.get(i))), bd_id);
        }
    }

    public static int getRegIdByKbId(String expertId) {
        String res = KBExpert_Detail.s_Detail(expertId);
        JSONObject r = JSONObject.fromObject(res);
        if(!r.getString("code").equals("1000000")) {
            logger.error(unicodeString(res));
            return 0;
        }
        return Integer.parseInt(r.getJSONObject("data").getString("register_id"));
    }

    public static String getServeIdByRegId(String regId) {
        String res = "";
        res = RegisteredDoctor_Detail.s_Detail(regId);
        JSONObject r = JSONObject.fromObject(res);
        if(!r.getString("code").equals("1000000")) {
            logger.error(unicodeString(res));
            return null;
        }
        if(r.getJSONObject("data").isEmpty()) {
            logger.error(unicodeString(res));
            return null;
        }
        return r.getJSONObject("data").getString("service_id");
    }
}
