package com.mingyizhudao.qa.functiontest.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by ttshmily on 26/4/2017.
 */
public class GetSurgeryCategory extends BaseTest {

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

        String userToken = "";
        HashMap<String,String> mainDoctorInfo = s_CreateSyncedDoctor(mainUser);
        if(mainDoctorInfo == null) {
            logger.error("创建注册专家失败，退出执行");
            System.exit(10000);
        }
        userToken = mainDoctorInfo.get("token");

        String res = "";
        res = HttpRequest.s_SendGet(host_doc+uri,"", userToken);
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():root_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():root"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch():id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch():parent_category_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch():name"));
    }

    @Test
    public void test_02_没有token信息的请求可以获得有效信息() {
        String res = "";
        res = HttpRequest.s_SendGet(host_doc + uri,"", "");
        s_CheckResponse(res);
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():root_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():root"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch()"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch():id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch():parent_category_id"));
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgeryCategories():branch():name"));
    }
}
