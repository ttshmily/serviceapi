package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Common_GetTitles extends BaseTest {

    public static final Logger logger= Logger.getLogger(Common_GetTitles.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors/titles";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_获取所有职级列表() {

        String res = "";

        try {
            res = HttpRequest.s_SendGet(host_crm+uri, "", crm_token, null);
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            int academicLen = Integer.parseInt(Generator.parseJson(data, "list:academic()"));
            int medicalLen = Integer.parseInt(Generator.parseJson(data, "list:medical()"));
            Assert.assertEquals(academicLen, 4);
            Assert.assertEquals(medicalLen, 12);

        } catch (IOException e) {
            logger.error(e);
        }
    }
}
