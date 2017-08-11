package com.mingyizhudao.qa.dataprofile;

import static com.mingyizhudao.qa.utilities.Generator.*;
import lombok.Data;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class Doctor {

    private String name;
    private int gender;
    private String birthday;
    private String major_id;
    private String hospital_id;
    private String department_name;
    private String academic_title_list;
    private String medical_title_list;
    private String start_year;
    private String honour;
    private String specialty;
    private String description;
    private List<Picture> avatar_url;
//    private boolean is_internet_operation_doctor;
    private String[] cooperation_channel;
    private String signed_status;
    private String city_id;
    private String county_id;

    public Doctor(String type) {
        if (type.equals("basic")) {
            this.name = "大一" + randomString(4);
            this.hospital_id = randomHospitalId();
            this.department_name = "随机科室" + randomString(2);
            this.medical_title_list = randomMedicalId();
        }
    }

    public Doctor() {
        this.name = "大一"+randomString(4);
        this.gender = (int)randomInt(2);
        this.birthday = randomDate("1949/10/01", "2017/03/13");
        this.major_id = randomMajorId();
        this.hospital_id = randomHospitalId();
        this.department_name = "随机科室"+randomString(2);
        this.medical_title_list = randomMedicalId();
        this.academic_title_list = randomAcademicId();
        this.start_year = randomDate("1949/10/01", "2017/03/13").substring(0,4);
        this.honour = "荣誉"+randomString(20);
        this.specialty = "特长"+randomString(90);
        this.description = "描述"+randomString(100);
//        this.is_internet_operation_doctor = true;
        this.cooperation_channel = new String[]{"WEIBO"};
        this.major_id = randomMajorId();
//        this.avatar_url = new ArrayList<Picture>(){{add(new Picture("123.jpb", "7")); add(new Picture("123.jpb", "7"));}};
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
