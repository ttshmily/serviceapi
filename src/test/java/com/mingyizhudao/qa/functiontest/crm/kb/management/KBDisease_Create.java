package com.mingyizhudao.qa.functiontest.crm.kb.management;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.Disease;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.mingyizhudao.qa.functiontest.crm.kb.management.KBDisease_Detail.s_Detail;
import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KBDisease_Create extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases";

    public static String s_Create(Disease d) {
        String res = "";
        TestLogger logger = new TestLogger(s_JobName());
        res = HttpRequest.s_SendPost(host_crm + uri, d.transform(), crm_token);
        return JSONObject.fromObject(res).getJSONObject("data").getString("id");
    }

    @Test
    public void test_01_创建疾病() {
        String res = "";

        Disease d = new Disease();
        res = HttpRequest.s_SendPost(host_crm + uri, d.transform(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String id = s_ParseJson(data, "id");
        res = s_Detail(id);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(s_ParseJson(data, "name"), d.getName());
        Assert.assertEquals(s_ParseJson(data, "description"), d.getDescription());
        Assert.assertEquals(s_ParseJson(data, "user_visible"), "true");
        Assert.assertEquals(s_ParseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(s_ParseJson(data, "is_common"), d.getIs_common().toString());
        Assert.assertEquals(s_ParseJson(data, "category_list(0):disease_category_id"), d.getCategory_list().get(0).getDisease_category_id());
    }

    @Test
    public void test_02_创建疾病_boolean取反() {
        String res = "";
        Disease d = new Disease();
        d.setIs_common(0);
        d.setUser_visible(0);
        res = HttpRequest.s_SendPost(host_crm + uri, d.transform(), crm_token);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");

        String id = s_ParseJson(data, "id");
        res = s_Detail(id);
        s_CheckResponse(res);
        Assert.assertNotNull(s_ParseJson(data, "id"), "医库ID不能少");
        Assert.assertEquals(s_ParseJson(data, "name"), d.getName());
        Assert.assertEquals(s_ParseJson(data, "description"), d.getDescription());
        Assert.assertEquals(s_ParseJson(data, "user_visible"), "false");
        Assert.assertEquals(s_ParseJson(data, "doctor_visible"), "true");
        Assert.assertEquals(s_ParseJson(data, "is_common"), d.getIs_common().toString());
        Assert.assertEquals(s_ParseJson(data, "category_list(0):disease_category_id"), d.getCategory_list().get(0).getDisease_category_id());
    }

    @Test
    public void test_03_创建疾病_缺少字段() {
        String res = "";

        Disease d = new Disease();
        d.setName(null);
        res = HttpRequest.s_SendPost(host_crm + uri, d.transform(), crm_token);
        s_CheckResponse(res);
        Assert.assertNotEquals(code, "1000000");
    }

}
