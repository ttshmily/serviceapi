package com.mingyizhudao.qa.functiontest.patient;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

import static com.mingyizhudao.qa.utilities.Helper.s_ParseJson;

/**
 * Created by TianJing on 2017/9/4.
 */
public class GetUploadToken extends BaseTest{
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String url = "/api/v2/getUploadToken";

    public static String s_getUploadToken(HashMap<String ,String> query){
        String res = "";
        res = HttpRequest.s_SendGet(host_patient + url, query, "");
        return JSONObject.fromObject(res).getJSONObject("data").getString("key");
    }

    @Test
    public void test_01_获取上传token(){
        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("type", "7");
        query.put("fileName", "测试3.jpg");
        System.out.println(query);
        res = HttpRequest.s_SendGet(host_patient + url, query, "");
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(s_ParseJson(data,"key"),"key值缺失");
        Assert.assertNotNull(s_ParseJson(data,"largeUrl"),"largeUrl值缺失");
        Assert.assertNotNull(s_ParseJson(data,"thumbnailUrl"),"thumbnailUrl值缺失");
        Assert.assertNotNull(s_ParseJson(data,"uploadInfo:driver"), "uploadInfo:driver值缺失");
        Assert.assertNotNull(s_ParseJson(data,"uploadInfo:key"), "uploadInfo:key值缺失");
        Assert.assertNotNull(s_ParseJson(data,"uploadInfo:token"), "uploadInfo:token值缺失");
    }
}
