package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.tc.doctor.Refresh;
import com.mingyizhudao.qa.tc.doctor.SendVerifyCode;
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

//    public String phone() {
//        Random random = new Random();
//        Integer m = random.nextInt(999999);
//        SendVerifyCode.mobile = "1" + String.format("%06d",m) + "9999";
//        return String.format("%06d",m);
//    }

}
