package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class RegisteredDoctor_List extends BaseTest{

    public static final Logger logger= Logger.getLogger(RegisteredDoctor_List.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/doctors";
    public static String mock = false ? "/mockjs/1" : "";

    public static int registeredDoctorList() {
        String res = "";
        try {
            res = HttpRequest.sendGet(host_crm + uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        return Integer.parseInt(UT.parseJson(JSONObject.fromObject(res), "data:size"));

    }
    @Test
    public void test_01_获取医生列表_使用默认值() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_crm+uri, "", crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(UT.parseJson(data, "list()"), "医生列表为空");
        Assert.assertNotEquals(UT.parseJson(data, "list(0):user_id"), "", "医生ID为空");
        Assert.assertNotEquals(UT.parseJson(data, "list(0):created_at"), "", "注册时间没有值");
        Assert.assertNotNull(UT.parseJson(data, "list(0):name"), "医生姓名为空");
        Assert.assertNotEquals(UT.parseJson(data, "list(0):mobile"), "", "医生手机为空");
        Assert.assertNotEquals(UT.parseJson(data, "list(0):hospital_name"), "", "医生医院名称为空");
        Assert.assertNotEquals(UT.parseJson(data, "list(0):hospital_id"), "", "医生医院ID为空");
        Assert.assertNotNull(UT.parseJson(data, "list(0):department"), "医生科室字段不存在");
        Assert.assertNotNull(UT.parseJson(data, "list(0):academic_title"), "医生学术职称不存在");
        Assert.assertNotNull(UT.parseJson(data, "list(0):medical_title"), "医生技术职称不存在");
        Assert.assertNotEquals(UT.parseJson(data, "list(0):is_verified"), "", "医生是否认证字段没有值");
        Assert.assertNotNull(UT.parseJson(data, "list(0):inviter_name"), "医生的地推字段不存在");
        Assert.assertNotNull(UT.parseJson(data, "list(0):is_famous"), "医生是否是专家字段不存在");
        Assert.assertNotNull(UT.parseJson(data, "size"), "列表总量字段不存在");
        Assert.assertEquals(UT.parseJson(data, "pagesize"), "10", "默认分页大小不为10");
        Assert.assertNotNull(UT.parseJson(data, "page"), "默认没有传回第1页");
        Assert.assertEquals(UT.parseJson(data, "list()"), "10", "分页的默认值不为10");
        Integer id1 = Integer.parseInt(UT.parseJson(data, "list(0):user_id"));
        Integer id2 = Integer.parseInt(UT.parseJson(data, "list(4):user_id"));
        Integer id3 = Integer.parseInt(UT.parseJson(data, "list(9):user_id"));

        query.put("page","2");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Integer id4 = Integer.parseInt(UT.parseJson(data, "list(0):user_id"));

        if (!(id1 > id2 && id2 > id3 && id3 > id4)) Assert.fail("没有按照医生ID倒序排列");

    }

    @Test
    public void test_02_获取医生列表_传入特定的页码和分页大小() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("page", "");
        query.put("page_size", "10");
        int total = registeredDoctorList();
        int pageSize = 10;
        int pageNum = total / pageSize + 1;
        // 默认分页大小10
        for (int i = 1; i < pageNum; i++) {
            query.replace("page",String.valueOf(i));
            try {
                res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(UT.parseJson(data, "list()"), "10", "分页的默认值不为10");
            Assert.assertEquals(UT.parseJson(data, "size"), String.valueOf(total), "列表总数量错误");
            Assert.assertEquals(UT.parseJson(data, "page"), String.valueOf(i), "页码错误");
        }

        // 设置分页大小为50
        for (int i = 0; i < 10; i++) {
            query.replace("page", "1");
            int page_size = (int) UT.randomInt(total);
            query.replace("page_size", String.valueOf(page_size));
            try {
                res = HttpRequest.sendGet(host_crm + uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(UT.parseJson(data, "list()"), String.valueOf(page_size), "分页值不正确");
            // 设置页数超出总量时，返回列表为空
            int page = total/page_size + 1;
            query.replace("page", String.valueOf(page+1));
            try {
                res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
            } catch (IOException e) {
                logger.error(e);
            }
            checkResponse(res);
            Assert.assertEquals(code, "1000000");
            Assert.assertEquals(UT.parseJson(data, "list()"), "0", "页码超出总数时，应当返回空列表");
        }

    }

    @Test
    public void test_03_获取医生列表_传入特定的医生姓名搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();

        //以姓名进行搜索
        query.put("doctor_name",mainDoctorName);
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("name").toLowerCase(), mainDoctorName.toLowerCase());
        }

        //更换搜索的姓名，确认搜索结果正确性
        query.replace("doctor_name","大二");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("name"), "大二");
        }

    }

    @Test
    public void test_04_获取医生列表_传入特定的医生手机搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("mobile",mainMobile);
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("mobile"), mainMobile);
        }

        query.replace("mobile","13817634203");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("mobile"), "13817634203");
        }

    }

    @Test
    public void test_05_获取医生列表_传入特定的地推姓名搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent","苏舒");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("agent","谢瑾");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_06_获取医生列表_传入特定的地推手机搜索条件() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("agent_mobile","13811112222");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.replace("agent_mobile","138111122222");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

    @Test
    public void test_07_获取医生列表_传入特定的医生姓名和认证状态() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("doctor_name","大一");
        query.put("certified_status","2");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        Pattern p = Pattern.compile("大一");

        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Matcher m = p.matcher(doc.getString("name"));
            Assert.assertEquals(doc.getString("is_verified"), "2");
            Assert.assertTrue(m.find(), "姓名搜索结果不准确");
        }

        query.replace("certified_status","1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Matcher m = p.matcher(doc.getString("name"));
            Assert.assertEquals(doc.getString("is_verified"), "1");
            Assert.assertTrue(m.find(), "姓名搜索结果不准确");
        }

        query.replace("certified_status","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Matcher m = p.matcher(doc.getString("name"));
            Assert.assertEquals(doc.getString("is_verified"), "-1");
            Assert.assertTrue(m.find(), "姓名搜索结果不准确");
        }

    }

    @Test
    public void test_08_获取医生列表_传入特定的地推姓名和认证状态() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("consultant_name","苏舒");
        query.put("certified_status","2");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("is_verified"), "2");
        }

        query.replace("certified_status","1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("is_verified"), "1");
        }

        query.replace("certified_status","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("is_verified"), "-1");
        }

    }

    @Test
    public void test_09_获取医生列表_传入特定的地推姓名和学术职称() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("consultant_name","苏舒");
        query.put("academic_title","ASSOCIATE_PROFESSOR");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("academic_title_list"), "ASSOCIATE_PROFESSOR");
        }

        query.replace("academic_title","PROFESSOR");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("academic_title_list"), "PROFESSOR");
        }

        query.replace("academic_title","-1");
        try {
            res = HttpRequest.sendGet(host_crm+mock+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO


    }

    @Test
    public void test_10_获取医生列表_传入特定的地推姓名和技术职称() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("consultant_name","苏舒");

        query.put("medical_title","ARCHIATER");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        JSONArray doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("medical_title_list"), "ARCHIATER");
        }

        query.replace("medical_title","ASSOCIATE_ARCHIATER");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO
        doc_list = data.getJSONArray("list");
        for (int i=0; i<doc_list.size(); i++) {
            JSONObject doc = doc_list.getJSONObject(i);
            Assert.assertEquals(doc.getString("inviter_name"), "苏舒");
            Assert.assertEquals(doc.getString("medical_title_list"), "ASSOCIATE_ARCHIATER");
        }

        query.replace("medical_title","-1");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000");
        //TODO
    }

    @Test
    public void test_11_获取医生列表_多选() {

        String res = "";
        HashMap<String, String> query = new HashMap<>();
        query.put("medical_title","ARCHIATER,ATTENDING_PHYSICIAN,CHIEF_NURSE");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("");
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertNotEquals(UT.parseJson(data, "list()"), "0");
        //TODO


        query.remove("medical_title");
        query.put("academic_title","PROFESSOR,ASSOCIATE_PROFESSOR");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("");
        }
        checkResponse(res);
        logger.info(HttpRequest.unicodeString(res));
        Assert.assertEquals(code, "1000000");
        Assert.assertNotEquals(UT.parseJson(data, "list()"), "0");

        //TODO

        query.remove("academic_title");
        query.put("certified_status","-1,2");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("");
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.put("academic_title","PROFESSOR,ASSOCIATE_PROFESSOR,LECTURER");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("");
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

        query.put("medical_title","ARCHIATER,ATTENDING_PHYSICIAN,CHIEF_NURSE");
        try {
            res = HttpRequest.sendGet(host_crm+uri, query, crm_token);
        } catch (IOException e) {
            logger.error(e);
            Assert.fail("");
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        //TODO

    }

}
