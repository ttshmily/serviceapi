package com.mingyizhudao.qa.tc;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 21/3/2017.
 */
public class HospitalSearch extends BaseTest {

    public static final Logger logger= Logger.getLogger(HospitalSearch.class);
    public static String uri = "/api/hospitalsearch";
    public static String mock = false ? "/mockjs/1" : "";

    public static String hospitalSearch() {
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
        Assert.assertNotNull(parseJson(data, "hospital()"));
    }

    @Test
    public void 没有searchName字段的请求可以获得有效信息() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "hospital()"));
    }

    @Test
    public void 查询字符串为空时的返回结果() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"searchname=", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "hospital()"));
    }

    @Test
    public void 查询字符串为中文时的返回结果() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"searchname=人民医院", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "hospital()"));
    }

    @Test
    public void 查询字符串为一串拼音时的返回结果() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"searchname=changzhou", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "hospital()"));
    }

    @Test
    public void 查询字符串为中英混合时的返回结果() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"searchname=中国changzhou", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "hospital()"));
    }

    @Test
    public void 返回的结果中详细字段不缺少() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host+mock+uri,"searchname=人民医院", "");
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotNull(parseJson(data, "hospital()"), "hospital字段缺失");
        Assert.assertNotNull(parseJson(data, "hospital():name"), "hospital的name字段缺失");
        Assert.assertNotNull(parseJson(data, "hospital():id"), "hospital的id字段缺失");
        Assert.assertNotNull(parseJson(data, "hospital():city"), "hospital的city字段缺失");
        Assert.assertNotNull(parseJson(data, "hospital():ext"), "hospital的ext字段缺失");
//        Assert.assertNotNull(parseJson(data, "hospital():ext:surgery_list()"), "hospital的surgery字段为空");
    }

}
