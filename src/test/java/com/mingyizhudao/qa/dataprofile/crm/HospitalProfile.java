package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 27/5/2017.
 */
public class HospitalProfile {

    public JSONObject body = new JSONObject();
    public HospitalProfile(boolean init) {
        if (init) {
            String tmp = UT.randomString(16);
            body.put("name", "测试医库医院" + tmp);
            body.put("short_name", "测试短名" + tmp.substring(8));
            body.put("hospital_class_list", UT.randomKey(KB.kb_hospital_class));
            body.put("type_list", UT.randomKey(KB.kb_hospital_type));
            body.put("city_id", UT.randomCityId());
            body.put("county_id", UT.randomCountyId());
            body.put("phone", "" + UT.randomPhone());
            body.put("description", "医库医院描述" + UT.randomString(30));
        }
    }
}
