package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import static com.mingyizhudao.qa.functiontest.bdassistant.VisitDate.s_VisitDate;
import static com.mingyizhudao.qa.functiontest.bdassistant.VisitRecord.s_Detail;
import static com.mingyizhudao.qa.functiontest.bdassistant.VisitRecordCreate.s_Create;
import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendGet;

public class VisitRecordList extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/visitRecords";

    @Test
    public void test_01_获取拜访记录列表() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        for (String createDate:s_VisitDate(bda_session_staff)) {
            query.put("createDate", createDate);
            res = s_SendGet(host_bda + uri, query, bda_session_staff);

            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertNotEquals(data.getJSONArray("list").size(), 0);

        }
    }

    @Test
    public void test_02_获取拜访记录列表_地推经理() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        for (String createDate:s_VisitDate(bda_session)) {
            query.put("createDate", createDate);
            res = s_SendGet(host_bda + uri, query, bda_session);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertNotEquals(data.getJSONArray("list").size(), 0);
        }

        s_Create(bda_session_staff);
        query.put("staff_id", "SH0098");
        for (String createDate:s_VisitDate(bda_session_staff)) {
            query.put("createDate", createDate);
            String res1 = s_SendGet(host_bda + uri, query, bda_session);
            s_CheckResponse(res1);
            int size1 = data.getJSONArray("list").size();
            String res2 = s_SendGet(host_bda + uri, query, bda_token_staff);
            s_CheckResponse(res2);
            int size2 = data.getJSONArray("list").size();
            Assert.assertEquals(size1, size2);
        }
    }
}
