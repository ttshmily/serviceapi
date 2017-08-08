package com.mingyizhudao.qa.functiontest.crm.trading.surgery;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Helper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Created by dayi on 2017/7/3.
 */
public class Order_ModifyBrief extends BaseTest {

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version + "/orders/{orderNumber}/surgeryBrief";

    @Test
    public void test_01_上传小结图片() {

        String res = "";
        String orderId = Order_List.s_SelectBriefedOrder();
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderId);
        JSONObject body = new JSONObject();
        JSONArray pics = JSONArray.fromObject("[{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'2'},{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102738.jpg';'type':'2'},{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102739.jpg';'type':'2'}]");
        body.put("surgery_brief_pictures",pics);
        res = HttpRequest.s_SendPost(host_crm+uri, body.toString(), crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Order_Detail.s_Detail(orderId);
        s_CheckResponse(res);
        Assert.assertEquals(Helper.s_ParseJson(data, "surgery_brief_pictures()"), "3");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgery_brief_pictures(0):key"), "2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg");
        Assert.assertEquals(Helper.s_ParseJson(data, "surgery_brief_pictures(0):type"), "2");
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgery_brief_pictures(0):thumbnailPicture"), "缺少缩略图");
        Assert.assertNotNull(Helper.s_ParseJson(data, "surgery_brief_pictures(0):largePicture"), "缺少大图");
    }

}
