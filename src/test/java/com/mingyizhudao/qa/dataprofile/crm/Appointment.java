package com.mingyizhudao.qa.dataprofile.crm;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mingyizhudao.qa.utilities.Generator.*;

@Data
public class Appointment {

    String expected_appointment_start_date;
    String expected_appointment_due_date;
    String expected_appointment_hospital_id;
    String expected_appointment_hospital_name;
    String expected_city_id;
    String expected_city_name;
    String expected_doctor_id;
    String expected_doctor_name;
    String expected_province_id;
    String expected_province_name;
    String major_disease_id;
    String major_disease_name;
    String disease_description;
    List<Picture> medical_record_pictures;

    int patient_age;
    int patient_gender;
    String patient_name;
    String patient_phone;

    String source_type;

    public Appointment() {
        this.patient_name = "面诊病人"+randomString(4);
        this.patient_age = (int)randomInt(90);
        this.patient_gender = (int)randomInt(2);
        this.patient_phone = randomPhone();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.expected_appointment_start_date = randomDateFromNow(2,3, df);
        this.expected_appointment_due_date = randomDateFromNow(3,8, df);

        String tmp = randomDiseaseId();
        this.major_disease_id = tmp;
        this.major_disease_name = diseaseName(tmp);
        this.disease_description = randomString(300);

        tmp = randomProvinceId();
        this.expected_province_id = tmp;
        this.expected_province_name = provinceName(tmp);

        tmp = randomCityIdUnder(tmp);
        this.expected_city_id = tmp;
        this.expected_city_name = cityName(tmp);

        tmp = randomExpertId();
        this.expected_doctor_id = tmp;
        this.expected_doctor_name = expertName(tmp);

        tmp = randomHospitalId();
        this.expected_appointment_hospital_id = tmp;
        this.expected_appointment_hospital_name = hospitalName(tmp);

        this.medical_record_pictures = new ArrayList<Picture>(){{add(new Picture("123.jpb", "7")); add(new Picture("123.jpb", "7"));}};

        String[] sources = new String[]{"BUSINESS", "HOT_LINE", "WEIBO", "BAIDU_BRIDGE", "SUSHU", "WECHAT", "PC_WEB"};
        Random random = new Random();
        this.source_type = sources[random.nextInt(sources.length)];
    }

    public String printPictures() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i =0; i < medical_record_pictures.size(); i++) {
            sb.append(medical_record_pictures.get(i).print());
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]");
        return sb.toString();
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
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\""+type+"\"");
            sb.append(",");
            sb.append("\"key\":\""+key+"\"");
            sb.append("}");
            return sb.toString();
        }
    }
}
