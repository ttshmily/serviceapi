package com.mingyizhudao.qa.dataprofile;

import lombok.Data;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mingyizhudao.qa.utilities.Generator.*;

@Data
public class Specialty {
    private category category;
    private List<disease> disease_list;

    public Specialty() {
        int disease_count = (int)randomInt(10);
        category = new category();
        disease_list = new ArrayList<disease>() {
            {
                for (int i = 0; i < disease_count; i++) {
                    add(new disease(category.getId()));
                }
            }
        };
    }

    @Data
    public class category {
        private String id;
        private String name;

        public category() {
            id = randomMajorId();
            name = majorName(id);
        }
    }

    @Data
    public class disease {
        private String id;
        private String name;

        public disease(String majorId) {
            id = randomDiseaseIdUnder(majorId);
            name = diseaseName(id);
        }
    }
}
