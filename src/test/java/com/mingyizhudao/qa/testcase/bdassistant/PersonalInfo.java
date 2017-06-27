package com.mingyizhudao.qa.testcase.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.doctor.DoctorProfile;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Detail;
import com.mingyizhudao.qa.testcase.crm.RegisteredDoctor_Modify;
import com.mingyizhudao.qa.testcase.doctor.CreateOrder;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ttshmily on 17/5/2017.
 */
//@Test(enabled = false)
public class PersonalInfo extends BaseTest {

    public static final Logger logger= Logger.getLogger(PersonalInfo.class);
    public static String uri = "/api/v1/user/personal";
    public static String mock = false ? "/mockjs/1" : "";

    @Test
    public void test_01_未登录用户无权限使用接口() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, map, "", null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertNotEquals(code, "1000000", "没有token不应该调用成功");

    }

    @Test
    public void test_02_主管用户_返回团队成员数量() {

        String res = "";
        HashMap<String, String> map = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, map, bda_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "有token应该调用成功");
        Assert.assertNotNull(parseJson(data, "doctorCounts"), "doctorCounts字段缺失");
        Assert.assertNotNull(parseJson(data, "orderCounts"), "orderCounts字段缺失");
        Assert.assertNotNull(parseJson(data, "teamMemberCounts"), "teamMemberCounts字段缺失");
        Assert.assertEquals(parseJson(data, "teamMemberCounts"), "6","teamMemberCounts字段值不正确");
        Assert.assertEquals(parseJson(data, "role"), "2"); // 2 表示主管

    }

    @Test
    public void test_03_主管用户_新建一个医生和订单() {

        String res = "";

        HashMap<String, String> map = new HashMap<>();
        try {
            res = HttpRequest.sendGet(host_bda + uri, map, bda_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        int doctorCountsBefore = Integer.parseInt(parseJson(data, "doctorCounts"));
        int orderCountsBefore = Integer.parseInt(parseJson(data, "orderCounts"));

        DoctorProfile dop = new DoctorProfile(true);
        HashMap<String, String> info = CreateVerifiedDoctor(dop);
        String doctorId = info.get("id");
        if (doctorId == null) {
            Assert.fail("创建医生失败，退出用例执行");
        }
        JSONObject dp = new JSONObject();
        dp.put("inviter_no", "SH0133");
        RegisteredDoctor_Modify.modify(doctorId, dp);
        res = RegisteredDoctor_Detail.Detail(doctorId);
        checkResponse(res);
        String inviter_no = parseJson(data, "inviter_no");
        if(!inviter_no.equals("SH0133")) Assert.fail("更新医生的invitor_no失败，退出用例执行");

        CreateOrder.CreateOrder(info.get("token"));
        try {
            res = HttpRequest.sendGet(host_bda + uri, map, bda_token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000");
        int doctorCountsAfter = Integer.parseInt(parseJson(data, "doctorCounts"));
        int orderCountsAfter = Integer.parseInt(parseJson(data, "orderCounts"));

        Assert.assertEquals(doctorCountsAfter, doctorCountsBefore + 1);
        Assert.assertEquals(orderCountsAfter, orderCountsBefore + 1);
    }

}
