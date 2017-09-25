package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.Hospital;
import static com.mingyizhudao.qa.utilities.Generator.*;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class KBHospital_Detail extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v2/medicallibrary/hospitals/{hospital_id}";

/*    public static HashMap<String, String> s_Detail(String hospitalId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        JSONObject hospital = null;
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        res = HttpRequest.s_SendGet(host_crm+uri,"", crm_token, pathValue);
        hospital = JSONObject.fromObject(res).getJSONObject("data");
        if (null == hospital) return null;
        HashMap<String, String> result = new HashMap<>();
        String cityId = hospital.containsKey("city_id") ? hospital.getString("city_id") : "";
        String countyId = hospital.containsKey("county_id") ? hospital.getString("county_id") : "";
        String name = hospital.containsKey("name") ? hospital.getString("name") : "";
        String short_name = hospital.containsKey("short_name") ? hospital.getString("short_name") : "";
        String type_list = hospital.containsKey("type_list") ? hospital.getString("type_list") : "";
        String hospital_class_list = hospital.containsKey("hospital_class_list") ? hospital.getString("hospital_class_list") : "";
        String city_name = hospital.containsKey("city_name") ? hospital.getString("city_name") : "";
        String county_name = hospital.containsKey("county_name") ? hospital.getString("county_name") : "";
        String phone = hospital.containsKey("phone") ? hospital.getString("phone") : "";
        String description = hospital.containsKey("description") ? hospital.getString("description") : "";
        String photo_url = hospital.containsKey("photo_url") ? hospital.getString("photo_url") : "";
        result.put("city_id", cityId);
        result.put("city_name", city_name);
        result.put("county_id", countyId);
        result.put("county_name", county_name);
        result.put("id", hospitalId);
        result.put("name", name);
        result.put("short_name", short_name);
        result.put("type_list", type_list);
        result.put("hospital_class_list", hospital_class_list);
        result.put("phone", phone);
        result.put("description", description);
        result.put("photo_url", photo_url);
        return result;
    }*/

    public static String s_Detail(String hospitalId) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", hospitalId);
        res = HttpRequest.s_SendGet(host_crm+uri,"", crm_token, pathValue);
        return res;
    }

    @Test
    public void test_01_获取医院详情_有效ID() {

        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id",info.get("id"));
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        Assert.assertEquals(Helper.s_ParseJson(data, "name"), hp.getName());
        Assert.assertEquals(Helper.s_ParseJson(data, "short_name"), hp.getShort_name());
        Assert.assertEquals(Helper.s_ParseJson(data, "hospital_class_list"), hp.getHospital_class_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "type_list"), hp.getType_list());
        Assert.assertEquals(Helper.s_ParseJson(data, "city_id"), hp.getCity_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "city_name"), cityName(hp.getCity_id()));
        Assert.assertEquals(Helper.s_ParseJson(data, "county_id"), hp.getCounty_id());
        Assert.assertEquals(Helper.s_ParseJson(data, "county_name"), countyName(hp.getCounty_id()));
        Assert.assertEquals(Helper.s_ParseJson(data, "phone"), hp.getPhone());
        Assert.assertEquals(Helper.s_ParseJson(data, "description"), hp.getDescription());
    }

    @Test
    public void test_02_获取医院详情_无效ID() {

        String res = "";
        Hospital hp = new Hospital();
        HashMap<String, String> info = KBHospital_Create.s_Create(hp);
        if (info == null) Assert.fail("创建医院失败，退出用例执行");
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("hospital_id", "111" + info.get("id"));
        res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }
}
