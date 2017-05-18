package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class DoctorProfile {

    public JSONObject body = new JSONObject();
    public DoctorProfile(boolean init) {
        JSONObject doctor = new JSONObject();
        if (init) {
            doctor.put("name", "庄恕" + UT.randomString(2));
            doctor.put("department", "胸外科");
            doctor.put("major_id", UT.randomKey(KB.kb_major));
            doctor.put("academic_title_list", UT.randomKey(KB.kb_academic_title));
            doctor.put("medical_title_list", UT.randomKey(KB.kb_medical_title));
            doctor.put("hospital_id", UT.randomKey(KB.kb_hospital));
            doctor.put("inviter_no", UT.randomEmployeeId());
        } else {
            doctor.put("name", "");
            doctor.put("department", "");
            doctor.put("major_id", "");
            doctor.put("academic_title_list", "");
            doctor.put("medical_title_list", "");
            doctor.put("hospital_id", "");
            doctor.put("inviter_no", "");
        }
        this.body.put("doctor",doctor);
    }
}