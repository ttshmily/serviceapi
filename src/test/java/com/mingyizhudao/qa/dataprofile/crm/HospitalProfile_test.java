package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 27/5/2017.
 */
public class HospitalProfile_test {

    public JSONObject body = new JSONObject();
    public HospitalProfile_test(boolean init) {
        if (init) {
            String tmp = Generator.randomString(16);
            body.put("name", "测试医库医院" + tmp);
            body.put("short_name", "测试短名" + tmp.substring(8));
            body.put("hospital_class_list", Generator.randomKey(KnowledgeBase.kb_hospital_class));
            body.put("type_list", Generator.randomKey(KnowledgeBase.kb_hospital_type));
            body.put("city_id", Generator.randomCityId());
            body.put("county_id", Generator.randomCountyId());
            body.put("phone", "" + Generator.randomPhone());
            body.put("description", "医库医院描述" + Generator.randomString(30));
        }
    }
}
