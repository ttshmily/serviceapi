package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class DiseaseProfile {
    public JSONObject body = new JSONObject();
    public DiseaseProfile(boolean init) {
        if (init) {
            String tmp = Generator.randomString(16);
            body.put("name", "病" + tmp);
            body.put("is_common", 1);
            body.put("description", "疾病描述" + Generator.randomString(30));
            body.put("user_visible", 1);
            JSONObject category = new JSONObject();
            category.put("disease_category_id", Generator.randomMajorId());
            body.put("category_list", JSONArray.fromObject(category));
        }
    }
}
