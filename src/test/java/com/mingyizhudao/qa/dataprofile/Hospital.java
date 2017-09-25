package com.mingyizhudao.qa.dataprofile;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.simplify;

import lombok.Data;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class Hospital {

    private String name;
    private String short_name;
    private String hospital_class_list;
    private String type_list;
    private String city_id;
    private String county_id;
    private String phone;
    private String description;
    private String address;
    private Integer hospital_level;
    private List<Picture> photo_url;

    public Hospital() {
        this.name = "新概念医院"+randomString(4);
        this.short_name = "新概念医院短名"+randomString(4);
        this.hospital_class_list = randomHospitalClass();
        this.type_list = randomHospitalType();
        this.phone = randomPhone();
        this.description = "新概念医院描述"+randomString(10);
        this.address = "医院地址"+randomString(10);
        this.hospital_level = (int)randomInt(3);

        String cityId = randomCityId();
        this.city_id = cityId;
        this.county_id = randomCountyIdUnder(cityId);
        this.photo_url = new ArrayList<Picture>() {
            {add(new Picture("1,jpg", "5", "1x1"));}
        };
    }

    public String transform() {
        return simplify(this).toString();
    }

    @Data
    public class Picture {
        String key;
        String type;
        String tag;
        public Picture(String key, String type, String tag) {
            this.key = key;
            this.type = type;
            this.tag = tag;
        }

        public String print() {
            return JSONObject.fromObject(this).toString();
        }
    }

}
