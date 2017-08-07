package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class KBHospital_List extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/v1/medicallibrary/hospitals";

    @Test
    public void test_01_获取医库医生列表_使用默认值() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        res = HttpRequest.s_SendGet(host_crm + uri, "", crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list()"), "医院列表为空");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):id"), "", "医院ID为空");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):name"), "", "医院姓名为空");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):hospital_class_list"), "", "医院等级");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):type_list"), "", "医院类型");
        Assert.assertNotEquals(Helper.s_ParseJson(data, "list(0):city_id"), "", "医院所在城市");
        Assert.assertNotNull(Helper.s_ParseJson(data, "list(0):phone"), "医院电话");
        Assert.assertNotNull(Helper.s_ParseJson(data, "size"), "列表总量字段不存在");
        Assert.assertEquals(Helper.s_ParseJson(data, "page_size"), "10", "默认分页大小不为10");
        Assert.assertNotNull(Helper.s_ParseJson(data, "page"), "默认没有传回第1页");
        Assert.assertEquals(Helper.s_ParseJson(data, "list()"), "10", "分页的默认值不为10");
        Integer id1 = Integer.parseInt(Helper.s_ParseJson(data, "list(0):id"));
        Integer id2 = Integer.parseInt(Helper.s_ParseJson(data, "list(4):id"));
        Integer id3 = Integer.parseInt(Helper.s_ParseJson(data, "list(9):id"));

        query.put("page","2");
        res = HttpRequest.s_SendGet(host_crm + uri, query, crm_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Integer id4 = Integer.parseInt(Helper.s_ParseJson(data, "list(0):id"));

        if (!(id1 > id2 && id2 > id3 && id3 > id4)) Assert.fail("没有按照医院ID倒序排列");
    }

    @Test
    public void test_02_获取医库医院列表_传入特定医院等级() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String hospitalClass = Generator.randomHospitalClass();
        query.put("hospitalClass", hospitalClass);
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray hos_list = data.getJSONArray("list");
        for (int i=0; i<hos_list.size(); i++) {
            JSONObject hos = hos_list.getJSONObject(i);
            Assert.assertEquals(hos.getString("hospital_class_list"), hospitalClass);
        }

        hospitalClass = Generator.randomHospitalClass().concat(",").concat(Generator.randomHospitalClass());
        query.replace("hospitalClass",hospitalClass);
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        hos_list = data.getJSONArray("list");
        for (int i=0; i<hos_list.size(); i++) {
            JSONObject hos = hos_list.getJSONObject(i);
            String title = hos.getString("hospital_class_list");
            Assert.assertTrue(hospitalClass.contains(title));
        }

        query.replace("hospitalClass","-1");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
    }

    @Test
    public void test_03_获取医库医院列表_传入特定医院类型() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String type = Generator.randomHospitalType();
        query.put("type", type);
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray hos_list = data.getJSONArray("list");
        for (int i=0; i<hos_list.size(); i++) {
            JSONObject hos = hos_list.getJSONObject(i);
            Assert.assertEquals(hos.getString("type_list"), type);
        }

        type = Generator.randomHospitalType().concat(",").concat(Generator.randomHospitalType());
        query.replace("type",type);
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        hos_list = data.getJSONArray("list");
        for (int i=0; i<hos_list.size(); i++) {
            JSONObject hos = hos_list.getJSONObject(i);
            String title = hos.getString("type_list");
            Assert.assertTrue(type.contains(title));
        }

        query.replace("type","-1");
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
    }

    @Test
    public void test_04_获取医库医院列表_传入省份ID() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        String provinceId = Generator.randomProvinceId();
        query.put("province", provinceId);
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray hos_list = data.getJSONArray("list");
        for (int i=0; i<hos_list.size(); i++) {
            JSONObject hos = hos_list.getJSONObject(i);
            Assert.assertEquals(hos.getString("province_id"), provinceId);
        }

        provinceId = Generator.randomProvinceId().concat(",").concat(Generator.randomProvinceId()).concat(",").concat(Generator.randomProvinceId()).concat(",").concat(Generator.randomProvinceId()).concat(",").concat(Generator.randomProvinceId()).concat(",").concat(Generator.randomProvinceId()).concat(",").concat(Generator.randomProvinceId());
        query.replace("province",provinceId);
        res = HttpRequest.s_SendGet(host_crm+uri, query, crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        hos_list = data.getJSONArray("list");
        for (int i=0; i<hos_list.size(); i++) {
            JSONObject hos = hos_list.getJSONObject(i);
            String title = hos.getString("province_id");
            Assert.assertTrue(provinceId.contains(title));
        }

    }

}
