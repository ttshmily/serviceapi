package com.mingyizhudao.qa.dataprofile;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.simplify;

import lombok.Data;

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

    public Disease() {
        this.name = "疾病"+randomString(3);
        this.is_common = 1;
        this.description = randomString(10);
        this.user_visible = 1;
        this.initial = randomString(1).toUpperCase();
        this.category_list = new ArrayList<Category>() {
            {
                int size = (int)randomInt(3);
                for(int i=0; i<size; i++){
                    add(new Category());
                }
            }
        };
    }

    public String transform() {
        return simplify(this).toString();
    }

    @Data
    public class Category {
        private String disease_category_id;
        public Category() {
            this.disease_category_id = randomMajorId();
        }
    }

}
