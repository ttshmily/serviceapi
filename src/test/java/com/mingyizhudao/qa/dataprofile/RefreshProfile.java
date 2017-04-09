package com.mingyizhudao.qa.dataprofile;

import com.mingyizhudao.qa.tc.Refresh;
import com.mingyizhudao.qa.tc.SendVerifyCode;
import net.sf.json.JSONObject;

import java.util.Random;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class RefreshProfile {

    public JSONObject body = new JSONObject();

    public RefreshProfile(boolean init) {
        if (init) {
            body.accumulate("token", Refresh.token);
        } else {
            body.accumulate("token", "");
        }
    }

    public String phone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        SendVerifyCode.mobile = "13" + String.format("%05d",m) + "9999";
        return String.format("%05d",m);
    }
}
