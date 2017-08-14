package com.mingyizhudao.qa.dataprofile;

import lombok.Data;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.mingyizhudao.qa.utilities.Generator.*;

@Data
public class SurgeryOrder {

    private OrderDetail order;

    public SurgeryOrder() {
        this.order = new OrderDetail();
    }

    @Data
    public class OrderDetail {
        private String patient_name;
        private int patient_gender;
        private int patient_age;
        private String patient_phone;
        private String major_disease_id;
        private String minor_disease_id;
        private String diagnosis;
        private String is_reoperation;
        private String expected_doctor_id;
        private String expected_surgery_hospital_id;
        private String expected_surgery_start_date;
        private String expected_surgery_due_date;
        private List<Picture> medical_record_pictures;

        public OrderDetail() {
            this.patient_name = "王二"+randomString(4);
            this.patient_gender = (int)randomInt(2);
            this.patient_age = (int)randomInt(100);
            this.patient_phone = randomPhone();
            this.major_disease_id = randomDiseaseId();
            this.minor_disease_id = randomDiseaseId();
            this.expected_doctor_id = randomExpertId();
            this.expected_surgery_hospital_id = randomHospitalId();
            this.expected_surgery_start_date = randomDateFromNow(1, 2, new SimpleDateFormat("yyyy-MM-dd"));
            this.expected_surgery_due_date = randomDateFromNow(2, 7, new SimpleDateFormat("yyyy-MM-dd"));
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
