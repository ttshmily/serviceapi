package com.mingyizhudao.qa.functiontest.login;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.dataprofile.login.RefreshProfile;
import com.mingyizhudao.qa.functiontest.doctor.GetDoctorProfile_V1;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONException;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by ttshmily on 22/3/2017.
 */
public class Refresh extends BaseTest{

    public static final Logger logger= Logger.getLogger(Refresh.class);
    public static String uri = "/api/login/refresh";
    public static String mock = false ? "/mockjs/1" : "";
    public static String mobile;
    public static String token;

    public static String refresh() {
        return "";
    }

    @Test
    public void 刷新token后返回新的token且老token依然可用() {
        String res = "";

        SendVerifyCode.send();
        CheckVerifyCode.check();
        // record the old token
        String oldToken = token;

        RefreshProfile body = new RefreshProfile(true);
        try {
            res = HttpRequest.sendPost(host_login +mock+uri,body.body.toString(), oldToken);
            checkResponse(res);
        } catch (IOException e) {

        } catch (JSONException e) {
        }
        Assert.assertEquals(code, "1000000");
        Assert.assertNotNull(Generator.parseJson(data,"token"), "token not exist");
        Assert.assertNotNull(Generator.parseJson(data, "expire"), "expire not exist");
        Assert.assertEquals(Generator.parseJson(data, "expire"), "7200");
        // update token if succeed
        token = Generator.parseJson(data, "token");
        CheckVerifyCode.token = token;

        // check old token still effective
        String r = GetDoctorProfile_V1.MyProfile(oldToken);
        checkResponse(r);
        Assert.assertEquals(code, "1000000", "old Token expired");
        String oldProfile = Generator.parseJson(data, "doctor");
        logger.debug(oldProfile);

        // check new token taking effect
        String s = GetDoctorProfile_V1.MyProfile(token);
        checkResponse(s);
        Assert.assertEquals(code, "1000000");
        String newProfile = Generator.parseJson(data, "doctor");
        logger.debug(newProfile);

        Assert.assertEquals(oldProfile, newProfile, "both token get the same profile");
    }

    @Test
    public void body中的token和http头中的token不一致() {
        String res = "";

        SendVerifyCode.send();
        CheckVerifyCode.check();

        // record the old token
        String oldToken = token;
        RefreshProfile body = new RefreshProfile(false);
        body.body.replace("token", mainToken);
        try {
            res = HttpRequest.sendPost(host_login +mock+uri,body.body.toString(), oldToken);
            checkResponse(res);
        } catch (IOException e) {
            Assert.fail();
        } catch (JSONException e) {
        }
        Assert.assertEquals(code, "1000000");

        // check token in body still effective
        String r = GetDoctorProfile_V1.MyProfile(mainToken);
        checkResponse(r);
        Assert.assertEquals(code, "1000000");
//        Assert.assertNotNull(parseJson(data,"token"), "token not exist");
//        Assert.assertNotNull(parseJson(data, "expire"), "expire not exist");
//        Assert.assertEquals(parseJson(data, "expire"), "600");
    }
}

