package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by dayi on 2017/7/7.
 */
public class SurgeryCategory extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/getsurgerycategory";

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories()"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():root_id"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():root"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch()"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch():id"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch():parent_category_id"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch():name"));
    }

    @Test
    public void test_02_没有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"","");
        } catch (IOException e) {
            logger.error(e);
        }
        s_CheckResponse(res);
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories()"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():root_id"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():root"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch()"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch():id"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch():parent_category_id"));
        Assert.assertNotNull(Generator.s_ParseJson(data, "surgeryCategories():branch():name"));
    }
}
