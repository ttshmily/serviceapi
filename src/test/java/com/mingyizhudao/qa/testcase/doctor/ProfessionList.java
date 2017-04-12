package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class ProfessionList extends BaseTest {

    public static final Logger logger= Logger.getLogger(ProfessionList.class);
    public static String uri = "/api/professionlist";
    public static String mock = false ? "/mockjs/1" : "";

    public static String professionList() {
        return "";
    }

    @Test
    public void 有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", mainToken);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "diseasecategory()"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():root_id"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():root"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch()"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch():id"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch():parent_category_id"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch():name"));
    }

    @Test
    public void 没有token信息的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "diseasecategory()"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():root_id"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():root"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch()"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch():id"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch():parent_category_id"));
        Assert.assertNotNull(parseJson(data, "diseasecategory():branch():name"));
    }

}
