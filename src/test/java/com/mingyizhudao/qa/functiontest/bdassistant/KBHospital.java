package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 17/5/2017.
 */
public class KBHospital extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/hospitals";

    @Test
    public void test_01_返回的结果中详细字段不缺少() {
        String res = "";
        HashMap<String, String> map = new HashMap<>();
        for (String hospitalName: new String[] {"安阳医院", "人民医院", "renmin", "常zhou"}) {
            logger.info("搜索字段：" + hospitalName);
            map.put("hospitalName", hospitalName);
            for (String token:new String[] {bda_session, bda_session_staff}) {
                res = HttpRequest.s_SendGet(host_bda + uri, map, token);
                s_CheckResponse(res);
                Assert.assertEquals(code, "1000000");
                JSONArray hospital_list = data.getJSONArray("list");
                Assert.assertNotEquals(hospital_list.size(), 0);
                Assert.assertNotNull(hospital_list.getJSONObject(0).getString("name"), "hospital的name字段缺失");
                Assert.assertNotNull(hospital_list.getJSONObject(0).getString("id"), "hospital的id字段缺失");
            }
        }

    }

}
