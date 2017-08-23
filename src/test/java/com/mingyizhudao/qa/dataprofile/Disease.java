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
    private int is_common;
    private String description;
    private int user_visible;
    private List<String> category;

    public Disease() {
        this.name = "疾病"+randomString(3);
        this.is_common = 1;
        this.description = randomString(10);
        this.user_visible = 1;
    }

    public String transform() {
        JsonConfig jc = new JsonConfig();
        jc.setAllowNonStringKeys(true);
        jc.setExcludes(getNullFieldName(this));
        return JSONObject.fromObject(this, jc).toString();
    }

}
