package com.mingyizhudao.qa.dataprofile.doctor;

import com.mingyizhudao.qa.util.Generator;
import net.sf.json.JSONObject;

/**
 * Created by dayi on 2017/7/3.
 */
public class SurgeryBrief {
    public JSONObject body = new JSONObject();
    public SurgeryBrief(boolean init) {
        JSONObject brief = new JSONObject();
        if (init) {
            brief.put("surgery_brief_hospital_id", Generator.randomHospitalId());
            brief.put("surgery_brief_date", Generator.randomDateTillNow());
            brief.put("surgery_brief_description", Generator.randomString(100));
            brief.put("surgery_brief_final_diagnosed_disease_id", Generator.randomDiseaseId());
            brief.put("surgery_brief_surgery_id", Generator.randomSurgeryId());
            brief.accumulate("surgery_brief_pictures", JSONObject.fromObject("{'key':'2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg';'type':'2'}"));
            brief.accumulate("surgery_brief_pictures", JSONObject.fromObject("{'key':'2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg';'type':'2'}"));
        } else {

        }
        this.body.put("order",brief);
    }
}
