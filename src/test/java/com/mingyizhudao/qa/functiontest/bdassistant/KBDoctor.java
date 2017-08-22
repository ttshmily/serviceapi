package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ttshmily on 17/5/2017.
 */
public class KBDoctor extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/doctor-kb";

    @Test
    public void test_01_获取医库医生列表_传入特定的医生姓名() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        for (String doctorName: new String[] {}) {
            logger.info("搜索姓名："+doctorName);
            query.put("doctorName", doctorName);
            res = HttpRequest.s_SendGet(host_bda+uri, query, bda_session_staff);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            JSONArray exp_list = data.getJSONArray("list");
            Assert.assertNotEquals(exp_list.size(), 0);
            Assert.assertNotEquals(exp_list.getJSONObject(0).getString("id"), "", "医生ID为空");
            Assert.assertNotEquals(exp_list.getJSONObject(0).getString("name"), "", "医生姓名为空");
            Assert.assertNotEquals(exp_list.getJSONObject(0).getString("hospital_id"), "", "医生医院ID为空");
            Assert.assertNotEquals(exp_list.getJSONObject(0).getString("hospital_name"), "", "医生医院名称为空");

            for (int i=0; i<exp_list.size(); i++) {
                JSONObject exp = exp_list.getJSONObject(i);
                Pattern p = Pattern.compile(doctorName);
                Matcher m = p.matcher(exp.getString("name"));
                Assert.assertTrue(m.find(), "姓名搜索结果不准确");
            }
        }
    }
}
