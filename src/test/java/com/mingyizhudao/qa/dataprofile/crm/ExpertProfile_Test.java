package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.common.KnowledgeBase;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONObject;

/**
 * Created by ttshmily on 24/5/2017.
 */
public class ExpertProfile_Test {

    public JSONObject body = new JSONObject();
    public ExpertProfile_Test(boolean init) {
        if (init) {
            body.put("name", "钟西北" + Generator.randomString(2));
            body.put("gender", Generator.randomInt(2));
            body.put("major_id", Generator.randomKey(KnowledgeBase.kb_major));
            body.put("hospital_id", Generator.randomKey(KnowledgeBase.kb_hospital));
            body.put("department", "胸外科");
            body.put("description", Generator.randomString(200));
            String date = Generator.randomDate("1949/10/01", "2017/03/13");
            body.put("birthday", date);
            body.put("start_year", Generator.randomDate(date, "2017/03/13").substring(0,4));
            body.put("specialty", Generator.randomString(100));
            body.put("academic_title_list", Generator.randomKey(KnowledgeBase.kb_academic_title));
            body.put("medical_title_list", Generator.randomKey(KnowledgeBase.kb_medical_title));
            body.put("honour", Generator.randomString(50));
        } else {

        }
    }
}
