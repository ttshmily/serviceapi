package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class DoctorProfile {

    public JSONObject body = new JSONObject();
    public DoctorProfile(boolean init) {
        JSONObject doctor = new JSONObject();
        if (init) {
            doctor.put("name", "庄恕" + UT.randomString(3));
            doctor.put("department", "胸外科");
            doctor.put("major_id", UT.randomMajorId());
            doctor.put("academic_title_list", UT.randomAcademicId());
            doctor.put("medical_title_list", UT.randomMedicalId());
            doctor.put("hospital_id", UT.randomHospitalId());
            doctor.put("inviter_no", UT.randomEmployeeId());
            doctor.put("exp_list", JSONArray.fromObject("[{\"category\": {\"id\": 6,\"name\": \"皮肤肿瘤\"},\"disease_list\": [{\"id\": 339,\"name\": \"早期乳腺癌\"},{\"id\": 336,\"name\": \"炎性乳腺癌\"}]},{\"category\": {\"id\": 7,\"name\": \"神经肿瘤\"},\"disease_list\": [{\"id\": 394,\"name\": \"垂体腺瘤\"},{\"id\": 393,\"name\": \"催乳素瘤\"}]}]"));
        } else {

        }
        this.body.put("doctor",doctor);
    }
}
