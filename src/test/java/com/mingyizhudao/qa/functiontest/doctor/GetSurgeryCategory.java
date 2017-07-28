package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 26/4/2017.
 */
public class GetSurgeryCategory extends BaseTest {

    public static final Logger logger= Logger.getLogger(GetSurgeryCategory.class);
    public static String uri = "/api/getsurgerycategory";

    @Test
    public void test_01_有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories()"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():root_id"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():root"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch()"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch():id"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch():parent_category_id"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch():name"));
    }

    @Test
    public void test_02_没有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.s_SendGet(host_doc + uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories()"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():root_id"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():root"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch()"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch():id"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch():parent_category_id"));
        Assert.assertNotNull(Generator.parseJson(data, "surgeryCategories():branch():name"));
    }
}
