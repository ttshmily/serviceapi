package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class OrderDetail {

    public JSONObject body = new JSONObject();
    public JSONArray pics = new JSONArray();

    public OrderDetail(boolean init) {
        JSONObject order = new JSONObject();
        if (init) {
            order.put("patient_name", "辣妹" + UT.randomString(4));
            order.put("patient_gender", "1");
            order.put("patient_age",  String.valueOf(UT.randomInt(100)));
            order.put("patient_phone", UT.randomPhone());
            order.put("major_disease_id", UT.randomKey(KB.kb_disease));
            order.put("minor_disease_id", UT.randomKey(KB.kb_disease));
            order.put("diagnosis", "病情描述"+UT.randomString(100));
            order.put("is_reoperation", UT.randomInt(2)-1);
            order.put("reoperation_description", "二次手术描述"+UT.randomString(100));
            order.put("expected_doctor_id", UT.randomExpertId());
            String date1 = UT.randomDateFromNow(2, 5);
            String date2 = UT.randomDateFromNow(6,30);
            order.put("expected_surgery_start_date", date1);
            order.put("expected_surgery_due_date", date2);
            order.put("expected_surgery_hospital_id", UT.randomKey(KB.kb_hospital));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'1'}"));
            order.accumulate("medical_record_pictures", JSONObject.fromObject("{'key':'2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg';'type':'1'}"));
        } else {

        }
        body.put("order",order);
    }

}
