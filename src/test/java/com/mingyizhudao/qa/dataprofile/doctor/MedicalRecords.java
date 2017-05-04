package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class MedicalRecords {

    public JSONObject body = new JSONObject();
    public JSONArray pics = new JSONArray();
    public MedicalRecords(boolean init) {
        JSONObject order = new JSONObject();
        if (init) {
            order.put("patient_name", "大二病人" + UT.randomString(4));
            order.put("patient_gender", "1");
            order.put("patient_age",  String.valueOf(UT.randomInt(100)));
            order.put("patient_phone", UT.randomPhone());
            order.put("major_disease_id", UT.randomKey(KB.kb_disease));
            order.put("minor_disease_id", UT.randomKey(KB.kb_disease));
            order.put("diagnosis", "病情描述"+UT.randomString(100));
            String date1 = UT.randomDateFromNow(2, 5);
            String date2 = UT.randomDateFromNow(6,30);
            order.put("expected_surgery_start_date", date1);
            order.put("expected_surgery_due_date", date2);
            order.put("expected_surgery_hospital_id", UT.randomKey(KB.kb_hospital));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'123';'type':'1'}"));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'456';'type':'1'}"));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'789';'type':'1'}"));
        } else {
            order.put("patient_name", "");
            order.put("patient_gender", "");
            order.put("patient_age", "");
            order.put("patient_phone", "");
            order.put("major_disease_id", "");
            order.put("minor_disease_id", "");
            order.put("diagnosis", "");
            order.put("expected_surgery_start_date", "");
            order.put("expected_surgery_due_date", "");
            order.put("expected_surgery_hospital_id", "");
            order.put("medical_record_pictures", pics);
        }
        this.body.put("order", order);
    }
}
