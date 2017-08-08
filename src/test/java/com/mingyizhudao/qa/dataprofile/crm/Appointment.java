package com.mingyizhudao.qa.dataprofile.crm;

import com.mingyizhudao.qa.utilities.Generator;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        this.patient_name = "面诊病人"+ Generator.randomString(4);
        this.patient_age = (int) Generator.randomInt(100);
        this.patient_gender = (int) Generator.randomInt(2);
        this.patient_phone = Generator.randomPhone();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.expected_appointment_start_date = Generator.randomDateFromNow(2,3, df);
        this.expected_appointment_due_date = Generator.randomDateFromNow(3,8, df);

        String tmp = Generator.randomDiseaseId();
        this.major_disease_id = tmp;
        this.major_disease_name = Generator.diseaseName(tmp);
        this.disease_description = Generator.randomString(300);

        tmp = Generator.randomProvinceId();
        this.expected_province_id = tmp;
        this.expected_province_name = Generator.provinceName(tmp);

        tmp = Generator.randomCityIdUnder(tmp);
        this.expected_city_id = tmp;
        this.expected_city_name = Generator.cityName(tmp);

        tmp = Generator.randomExpertId();
        this.expected_doctor_id = tmp;
        this.expected_doctor_name = Generator.expertName(tmp);

        tmp = Generator.randomHospitalId();
        this.expected_appointment_hospital_id = tmp;
        this.expected_appointment_hospital_name = Generator.hospitalName(tmp);

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
