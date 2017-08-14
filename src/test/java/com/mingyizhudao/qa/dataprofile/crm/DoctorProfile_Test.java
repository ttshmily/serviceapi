package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class DoctorProfile_Test {

    public JSONObject body = new JSONObject();
    public DoctorProfile_Test(boolean init) {
        JSONObject doctor = new JSONObject();
        if (init) {
            doctor.put("name", "庄恕" + Generator.randomString(3));
            doctor.put("department", "胸外科");
            doctor.put("major_id", Generator.randomMajorId());
            doctor.put("academic_title_list", Generator.randomAcademicId());
            doctor.put("medical_title_list", Generator.randomMedicalId());
            doctor.put("hospital_id", Generator.randomHospitalId());
            doctor.put("inviter_no", Generator.randomEmployeeId());
            doctor.put("exp_list", JSONArray.fromObject("[{\"category\": {\"id\": 6,\"name\": \"皮肤肿瘤\"},\"disease_list\": [{\"id\": 339,\"name\": \"早期乳腺癌\"},{\"id\": 336,\"name\": \"炎性乳腺癌\"}]},{\"category\": {\"id\": 7,\"name\": \"神经肿瘤\"},\"disease_list\": [{\"id\": 394,\"name\": \"垂体腺瘤\"},{\"id\": 393,\"name\": \"催乳素瘤\"}]}]"));
        } else {

        }
        this.body.put("doctor",doctor);
    }
}
