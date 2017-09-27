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

import static com.mingyizhudao.qa.utilities.HttpRequest.s_SendGet;

public class UserCenter extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/userCenter";

    @Test
    public void test_01_获取bd_staff的用户中心() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();

        for (String token: new String[] {bda_session_staff}) {
            res = s_SendGet(host_bda + uri, query, token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");

//            Assert.assertNotNull(data.getString("distributed_count"));
            Assert.assertNotNull(data.getString("doctor_count"));
            Assert.assertNotNull(data.getString("is_bd_manager"));
            Assert.assertNotNull(data.getString("order_count"));
            Assert.assertNotNull(data.getString("todo"));
            Assert.assertNotNull(data.getString("user_avatar_url"));
//            Assert.assertNotNull(data.getString("user_district"));
            Assert.assertNotNull(data.getString("user_id"));
            Assert.assertNotNull(data.getString("user_name"));
            Assert.assertNotNull(data.getString("user_qrcode"));
        }
    }

    @Test
    public void test_02_bdManager获取bd_staff的用户中心() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("referrerId", "SH0143");
        res = s_SendGet(host_bda + uri, query, bda_session);
        s_CheckResponse(res);
        String data1 = data.toString();

        res = s_SendGet(host_bda + uri, "", bda_session_staff);
        s_CheckResponse(res);
        String data2 = data.toString();
        Assert.assertEquals(data1, data2);

    }

    @Test
    public void test_03_获取bd_manager的用户中心() {
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        for (String token: new String[] {bda_session}) {
            res = s_SendGet(host_bda + uri, query, token);
            s_CheckResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertNotNull(data.getString("user_qrcode"));
            Assert.assertNotNull(data.getString("doctor_count"));
            Assert.assertNotNull(data.getString("is_bd_manager"));
            Assert.assertNotNull(data.getString("order_count"));
            Assert.assertNotNull(data.getString("todo"));
            Assert.assertNotNull(data.getString("user_avatar_url"));
//            Assert.assertNotNull(data.getString("user_district"));
            Assert.assertNotNull(data.getString("user_id"));
            Assert.assertNotNull(data.getString("user_name"));
        }
    }

    @Test(enabled = false)
    public void test_04_个人负责城市信息的校验(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = s_SendGet(host_bda + uri, query, bda_session_staff);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");

        JSONArray bd_city_list = JSONObject.fromObject(data).getJSONArray("user_district");
        for (int j=0; j<bd_city_list.size(); j++) {
            Assert.assertNotNull(bd_city_list.getJSONObject(j).getString("city_id"));
            Assert.assertNotNull(bd_city_list.getJSONObject(j).getString("city_name"));
        }
    }
}
