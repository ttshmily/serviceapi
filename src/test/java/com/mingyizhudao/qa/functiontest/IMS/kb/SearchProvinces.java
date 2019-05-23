package com.mingyizhudao.qa.functiontest.IMS.kb;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by TianJing on 2017/10/26.
 */
public class SearchProvinces extends BaseTest{
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/provinces";

    @Test
    public void test_01_获取省份列表() {
        String res = "";
        logger.info("查询所有的省份");
        res = HttpRequest.s_SendGet(host_ims + uri, "", crm_token, null);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "没有返回省份列表");
    }
}
