package com.mingyizhudao.qa.dataprofile;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.*;
import lombok.Data;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class User {

    private UserDetail doctor;

    public User() {
        this.doctor = new UserDetail();
    }

    public String transform() {
        JsonConfig jc = new JsonConfig();
        jc.setExcludes(getNullFieldName(this));
        return JSONObject.fromObject(this, jc).toString();
    }

    @Data
    public class UserDetail {

        private String name;
        private String department;
        private String academic_title_list;
        private String medical_title_list;
        private String hospital_id;
        private List<Picture> doctor_card_pictures;
        private String inviter_no;
        private List<Specialty> exp_list;
        private String major_id;

        public String transform() {
            JsonConfig jc = new JsonConfig();
            jc.setExcludes(getNullFieldName(this));
            return JSONObject.fromObject(this, jc).toString();
        }

        public UserDetail() {
            this.name = "田静"+randomString(4);
            this.department = "大科室"+randomString(2);
            this.academic_title_list = randomAcademicId();
            this.medical_title_list = randomMedicalId();
            this.hospital_id = randomHospitalId();
            this.major_id = randomMajorId();
            this.inviter_no = randomEmployeeId();
        }

        @Data
        public class Picture {
            String key;
            String type;
            public Picture(String key, String type) {
                this.key = key;
                this.type = type;
            }

            public String print() {
                return JSONObject.fromObject(this).toString();
            }
        }
    }
}



