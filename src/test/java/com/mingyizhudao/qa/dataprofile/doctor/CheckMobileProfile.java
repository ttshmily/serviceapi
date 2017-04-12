package com.mingyizhudao.qa.dataprofile.doctor;

import net.sf.json.JSONObject;

import java.util.Random;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class CheckMobileProfile {

    public JSONObject body = new JSONObject();
    public CheckMobileProfile(boolean init) {
        if (init) {
            body.accumulate("mobile", "13" + phone() + "9999");
            body.accumulate("code", "123456");
            body.accumulate("state", "niyaowoa");
        } else {
            body.accumulate("mobile", "");
            body.accumulate("code", "");
            body.accumulate("state", "test");
        }
    }

    public String phone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        return String.format("%05d",m);

    }

}
