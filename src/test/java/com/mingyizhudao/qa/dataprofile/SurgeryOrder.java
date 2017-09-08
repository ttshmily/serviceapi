package com.mingyizhudao.qa.dataprofile;

import lombok.Data;
import net.sf.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.*;

@Data
public class SurgeryOrder {

    private OrderDetail order;

    public SurgeryOrder(String type) {
        this.order = new OrderDetail(type);
    }

    public String transform() {
        HashMap<String, Object> a = new HashMap<>();
        for (String o:getNotNullFieldName(this)) {
            a.put(o, simplify(getFieldValueByName(o, this)));
        }
        return JSONObject.fromObject(a).toString();
    }

    @Data
    public class OrderDetail {
        private String patient_name;
        private Integer patient_gender;
        private Integer patient_age;
        private String patient_phone;
        private String major_disease_id;
        private String minor_disease_id;
        private String diagnosis;
        private Integer is_reoperation;
        private String expected_doctor_id;
        private String expected_surgery_hospital_id;
        private String expected_surgery_start_date;
        private String expected_surgery_due_date;
        private List<Picture> medical_record_pictures;

        private String surgery_brief_hospital_id;
        private String surgery_brief_date;
        private String surgery_brief_description;
        private String surgery_brief_final_diagnosed_disease_id;
        private String surgery_brief_surgery_id;
        private List<Picture> surgery_brief_pictures;

        public OrderDetail(String type) {
            switch (type) {
                case "order": {
                    this.patient_name = "王二" + randomString(4);
                    this.patient_gender = (int) randomInt(2);
                    this.patient_age = (int) randomInt(100);
                    this.patient_phone = randomPhone();
                    this.major_disease_id = randomDiseaseId();
                    this.minor_disease_id = randomDiseaseId();
                    this.expected_doctor_id = randomExpertId();
                    this.is_reoperation = (int) randomInt(2) - 1;
                    this.expected_surgery_hospital_id = randomHospitalId();
                    this.expected_surgery_start_date = randomDateFromNow(1, 2, new SimpleDateFormat("yyyy-MM-dd"));
                    this.expected_surgery_due_date = randomDateFromNow(2, 7, new SimpleDateFormat("yyyy-MM-dd"));
                    this.diagnosis = randomString(300);
                    this.medical_record_pictures = new ArrayList<Picture>() {
                        {
                            add(new Picture("2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg", "1"));
                            add(new Picture("2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg", "1"));
                        }
                    };
                    break;
                }
                case "brief": {
                    this.surgery_brief_hospital_id = randomHospitalId();
                    this.surgery_brief_date = randomDateTillNow();
                    this.surgery_brief_description = randomString(100);
                    this.surgery_brief_final_diagnosed_disease_id = randomDiseaseId();
                    this.surgery_brief_surgery_id = randomSurgeryId();
                    this.surgery_brief_pictures = new ArrayList<Picture>(){
                        {
                            add(new Picture("2017/05/04/1265834e-97d8-44a0-95e7-047c7facaee8/IMG_20170429_102737.jpg", "2"));
                            add(new Picture("2017/05/04/1315bbe0-2836-4776-8216-ec55044f32dd/IMG_20161013_172442.jpg","2"));
                        }
                    };
                    break;
                }
                default: {

                }
            }
        }

        public String transform() {
            return simplify(this).toString();
        }

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
