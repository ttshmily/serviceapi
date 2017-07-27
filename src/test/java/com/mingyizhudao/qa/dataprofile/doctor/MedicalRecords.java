package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.util.Generator;
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
            order.put("patient_name", "美女" + Generator.randomString(4));
            order.put("patient_gender", "1");
            order.put("patient_age",  String.valueOf(Generator.randomInt(100)));
            order.put("patient_phone", Generator.randomPhone());
            order.put("major_disease_id", Generator.randomKey(KnowledgeBase.kb_disease));
            order.put("minor_disease_id", Generator.randomKey(KnowledgeBase.kb_disease));
            order.put("diagnosis", "病情描述"+ Generator.randomString(100));
            String date1 = Generator.randomDateFromNow(2, 5);
            String date2 = Generator.randomDateFromNow(6,30);
            order.put("expected_surgery_start_date", date1);
            order.put("expected_surgery_due_date", date2);
            order.put("expected_surgery_hospital_id", Generator.randomKey(KnowledgeBase.kb_hospital));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'1'}"));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg';'type':'1'}"));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/4ab279ba-4626-4491-abee-25029d2341d6/WechatIMG560.jpeg';'type':'1'}"));
        } else {
//            order.put("patient_name", "");
//            order.put("patient_gender", "");
//            order.put("patient_age", "");
//            order.put("patient_phone", "");
//            order.put("major_disease_id", "");
//            order.put("minor_disease_id", "");
//            order.put("diagnosis", "");
//            order.put("expected_surgery_start_date", "");
//            order.put("expected_surgery_due_date", "");
//            order.put("expected_surgery_hospital_id", "");
            order.put("medical_record_pictures", pics);
        }
        this.body.put("order", order);
    }
}
