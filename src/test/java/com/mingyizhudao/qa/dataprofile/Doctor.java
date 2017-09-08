package com.mingyizhudao.qa.dataprofile;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.simplify;

import lombok.Data;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
public class Doctor {

    private String name;
    private Integer gender;
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
    private Integer doctor_level;
    private Integer comprehensive_level;
    private String department_category_id;
    private List<Specialty> specialty_list;

    public Doctor(String t) {
        switch (t) {
            case "specialty_list": {
                int size = (int) randomInt(4);
                specialty_list = new ArrayList<Specialty>() {
                    {
                        for (int i = 0; i < size; i++) {
                            add(new Specialty());
                        }
                    }
                };
                break;
            }
            case "avatar_url": {
                this.avatar_url = new ArrayList<Picture>() {
                    {
                        int size = (int) randomInt(3);
                        for (int i = 0; i < size; i++) {
                            add(new Picture("123.jpg", "4"));
                        }
                    }
                };
                break;
            }
            default: {
                String type = randomHospitalType();
                this.name = "大一"+randomString(4);
                this.gender = (int)randomInt(2);
                this.birthday = randomDate("1949/10/01", "2017/03/13");
                this.hospital_id = randomHospitalIdWithType(type);
                this.department_name = "随机科室"+randomString(2);
                this.medical_title_list = randomMedicalId();
                this.academic_title_list = randomAcademicId();
                this.start_year = randomDate("1949/10/01", "2017/03/13").substring(0,4);
                this.honour = "荣誉"+randomString(20);
                this.specialty = "特长"+randomString(90);
                this.description = "描述"+randomString(100);
                this.cooperation_channel = new String[]{"WEIBO"};
                this.department_category_id = randomDepartmentIdUnder(type);
                this.doctor_level = (int)randomInt(4);
                this.comprehensive_level = (int)randomInt(10);
            }
        }
    }

    public String transform() {
        return simplify(this).toString();
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
