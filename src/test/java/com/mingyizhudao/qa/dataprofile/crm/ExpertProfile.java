package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.common.KB;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class ExpertProfile {

    public JSONObject body = new JSONObject();
    public ExpertProfile(boolean init) {
        if (init) {
            body.put("name", "钟西北" + UT.randomString(2));
            body.put("gender", UT.randomInt(2));
            body.put("major_id", UT.randomKey(KB.kb_major));
            body.put("hospital_id", UT.randomKey(KB.kb_hospital));
            body.put("department", "胸外科");
            body.put("description", UT.randomString(200));
            String date = UT.randomDate("1949-10-1", "2017-03-13");
            body.put("birthday", date);
            body.put("start_year", UT.randomDate(date, "2017-03-13").substring(0,4));
            body.put("specialty", UT.randomString(100));
            body.put("academic_title_list", UT.randomKey(KB.kb_academic_title));
            body.put("medical_title_list", UT.randomKey(KB.kb_medical_title));
            body.put("honour", UT.randomString(50));
        } else {

        }
    }
}
