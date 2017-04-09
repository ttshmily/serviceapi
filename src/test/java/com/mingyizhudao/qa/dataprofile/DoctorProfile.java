package com.mingyizhudao.qa.dataprofile;

import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class DoctorProfile {

    public JSONObject body = new JSONObject();
    public DoctorProfile(boolean init) {
        JSONObject doctor = new JSONObject();
        if (init) {
            doctor.accumulate("name", "test");
            doctor.accumulate("city_name", "上海");
            doctor.accumulate("department", "骨科");
            doctor.accumulate("major_id", "33");
            doctor.accumulate("major_name", "烧伤");
            doctor.accumulate("academic_title", "学习");
            doctor.accumulate("medical_title", "工程师");
            doctor.accumulate("hospital_id", "3");
            doctor.accumulate("hospital_name", "上海医院");
            doctor.accumulate("inviter_no", "GD0001");
            doctor.accumulate("inviter_name", "黄燕");
        } else {
            doctor.accumulate("name", "");
            doctor.accumulate("city_name", "");
            doctor.accumulate("department", "");
            doctor.accumulate("major_id", "");
            doctor.accumulate("major_name", "");
            doctor.accumulate("academic_title", "");
            doctor.accumulate("medical_title", "");
            doctor.accumulate("hospital_id", "");
            doctor.accumulate("hospital_name", "");
            doctor.accumulate("inviter_no", "");
            doctor.accumulate("inviter_name", "");
        }
        this.body.accumulate("doctor",doctor);
    }
}
