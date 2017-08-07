package com.mingyizhudao.qa.functiontest.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.login.RefreshProfile;
import com.mingyizhudao.qa.functiontest.doctor.GetDoctorProfile_V1;
import com.mingyizhudao.qa.utilities.Helper;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class Refresh extends BaseTest{

    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static String uri = "/api/login/refresh";
    public static String mobile;
    public static String token;

    public static String s_Refresh() {
        return "";
    }

    @Test
    public void 刷新token后返回新的token且老token依然可用() {
        String res = "";

        SendVerifyCode.s_Send();
        CheckVerifyCode.s_Check();
        // record the old token
        String oldToken = token;

        RefreshProfile body = new RefreshProfile(true);
        try {
            res = HttpRequest.s_SendPost(host_login + uri,body.body.toString(), oldToken);
            s_CheckResponse(res);
        } catch (IOException e) {

        } catch (JSONException e) {
        }
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Helper.s_ParseJson(data,"token"), "token not exist");
        Assert.assertNotNull(Helper.s_ParseJson(data, "expire"), "expire not exist");
        Assert.assertEquals(Helper.s_ParseJson(data, "expire"), "7200");
        // update token if succeed
        token = Helper.s_ParseJson(data, "token");
        CheckVerifyCode.token = token;

        // s_Check old token still effective
        String r = GetDoctorProfile_V1.s_MyProfile(oldToken);
        s_CheckResponse(r);
        Assert.assertEquals(code, "1000000", "old Token expired");
        String oldProfile = Helper.s_ParseJson(data, "doctor");
        logger.debug(oldProfile);

        // s_Check new token taking effect
        String s = GetDoctorProfile_V1.s_MyProfile(token);
        s_CheckResponse(s);
        Assert.assertEquals(code, "1000000");
        String newProfile = Helper.s_ParseJson(data, "doctor");
        logger.debug(newProfile);

        Assert.assertEquals(oldProfile, newProfile, "both token get the same profile");
    }

    @Test
    public void body中的token和http头中的token不一致() {
        String res = "";

        SendVerifyCode.s_Send();
        CheckVerifyCode.s_Check();

        // record the old token
        String oldToken = token;
        RefreshProfile body = new RefreshProfile(false);
        body.body.replace("token", mainToken);
        try {
            res = HttpRequest.s_SendPost(host_login + uri,body.body.toString(), oldToken);
            s_CheckResponse(res);
        } catch (IOException e) {
            Assert.fail();
        } catch (JSONException e) {
        }
        Assert.assertEquals(code, "1000000");

        // s_Check token in body still effective
        String r = GetDoctorProfile_V1.s_MyProfile(mainToken);
        s_CheckResponse(r);
        Assert.assertEquals(code, "1000000");
    }
}

