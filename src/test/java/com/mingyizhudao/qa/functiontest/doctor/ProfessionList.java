package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class ProfessionList extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/professionlist";

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():root_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():root"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch():id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch():parent_category_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch():name"));
    }

    @Test
    public void test_02_没有token信息的请求可以获得有效信息() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():root_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():root"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch():id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch():parent_category_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "diseasecategory():branch():name"));
    }

}
