package com.mingyizhudao.qa.dataprofile;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.getNullFieldName;

import lombok.Data;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import java.util.ArrayList;
import java.util.List;

@Data
public class Disease {
    private String name;
    private Integer is_common;
    private String description;
    private String initial;
    private Integer user_visible;
    private List<Category> category_list;

    @Data
    public class Category {
        private String disease_category_id;
        public Category() {
            this.disease_category_id = randomMajorId();
        }
    }

    public Disease() {
        this.name = "疾病"+randomString(3);
        this.is_common = 1;
        this.description = randomString(10);
        this.user_visible = 1;
        this.initial = randomString(1).toUpperCase();
        this.category_list = new ArrayList<Category>() {
            {
                add (new Category());
                add (new Category());
            }
        };
    }

    public String transform() {
        JsonConfig jc = new JsonConfig();
        jc.setAllowNonStringKeys(true);
        jc.setExcludes(getNullFieldName(this));
        return JSONObject.fromObject(this, jc).toString();
    }

}
