package com.mingyizhudao.qa.dataprofile;

import com.mingyizhudao.qa.functiontest.patient.PatientSendVerifyCode;
import lombok.Data;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mingyizhudao.qa.utilities.Generator.*;
import static com.mingyizhudao.qa.utilities.Helper.simplify;

@Data
public class AppointmentTask {


    //add by tianjing 2017/09/01
    private String code;

    // 就诊意向
    private String service_type;  // 专家提供的服务类型
    private String expected_city_id;
    private String expected_appointment_start_date;
    private String expected_appointment_due_date;

    private String expected_appointment_hospital_id;
    private String expected_appointment_hospital_name;
    private boolean expected_appointment_hospital_alternative;

    private String expected_doctor_id;
    private String expected_doctor_name;
    private boolean expected_doctor_alternative;

    // 患者信息
    private Integer patient_age;
    private Integer patient_gender;
    private String patient_name;
    private String patient_phone;
    private String patient_city_id;
    private String patient_province_id;

    private String disease_id;
    private String disease_name;
    private String disease_description;
    private boolean indications;

    private String previous_doctor_suggest;
    private String previous_hospital_id;
    private String previous_hospital_name;
    private String previous_appointment_date;

    // 工单基础信息
    private String source_type; // 来源：400，PC
    private List<String> source_channel;  // 了解方式


    private String doctor_id;
    private String doctor_user_id;
    private String doctor_name;
    private String doctor_phone;
    private String doctor_medical_title;
    private String doctor_academic_title;
    private String doctor_hospital;
    private String doctor_department;
    private String appointment_fee;
    private String doctor_fee;
    private String platform_fee;
    private String appointment_date;

    public AppointmentTask() {
        this.patient_name = "面诊病人"+randomString(4);
        this.patient_age = (int)randomInt(100);
        this.patient_gender = (int)randomInt(2);
        this.patient_phone = randomPhone();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.expected_appointment_start_date = randomDateFromNow(2,3, df);
        this.expected_appointment_due_date = randomDateFromNow(3,8, df);

        String tmp = randomDiseaseId();
        this.disease_id = tmp;
        this.disease_name = diseaseName(tmp);
        this.disease_description = randomString(300);

        tmp = randomProvinceId();
        tmp = randomCityIdUnder(tmp);
        this.expected_city_id = tmp;

        tmp = randomExpertId();
        this.expected_doctor_id = tmp;
        this.expected_doctor_name = expertName(tmp);

        tmp = randomHospitalId();
        this.expected_appointment_hospital_id = tmp;
        this.expected_appointment_hospital_name = hospitalName(tmp);

        String[] sources = new String[]{"BUSINESS", "HOT_LINE", "WEIBO", "BAIDU_BRIDGE", "SUSHU", "WECHAT", "PC_WEB", "MINGYIHUI", "RED_BIRD"};
        Random random = new Random();
        this.source_type = sources[random.nextInt(sources.length)];
    }

    public AppointmentTask(String from) {
        switch (from) {
            case "patient": {
                this.patient_name = "面诊病人"+randomString(4);
                this.patient_age = (int)randomInt(100);
                this.patient_gender = (int)randomInt(2);
                this.patient_phone = randomPhone();
                PatientSendVerifyCode.s_SendVerifyCode(patient_phone);
                this.code = "123456";

                String tmp = randomDiseaseId();
                this.disease_id = tmp;
                this.disease_name = diseaseName(tmp);
                this.disease_description = randomString(300);

                String[] sources = new String[]{"BUSINESS", "HOT_LINE", "WEIBO", "BAIDU_BRIDGE", "SUSHU", "WECHAT", "PC_WEB", "MINGYIHUI", "RED_BIRD"};
                Random random = new Random();
                this.source_type = sources[random.nextInt(sources.length)];
                break;
            }
            case "empty": {

            }
            default: {
                this.patient_name = "面诊病人"+randomString(4);
                this.patient_age = (int)randomInt(100);
                this.patient_gender = (int)randomInt(2);
                this.patient_phone = randomPhone();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                this.expected_appointment_start_date = randomDateFromNow(2,3, df);
                this.expected_appointment_due_date = randomDateFromNow(3,8, df);

                String tmp = randomDiseaseId();
                this.disease_id = tmp;
                this.disease_name = diseaseName(tmp);
                this.disease_description = randomString(300);

                tmp = randomProvinceId();
                tmp = randomCityIdUnder(tmp);
                this.expected_city_id = tmp;

                tmp = randomExpertId();
                this.expected_doctor_id = tmp;
                this.expected_doctor_name = expertName(tmp);

                tmp = randomHospitalId();
                this.expected_appointment_hospital_id = tmp;
                this.expected_appointment_hospital_name = hospitalName(tmp);

                String[] sources = new String[]{"BUSINESS", "HOT_LINE", "WEIBO", "BAIDU_BRIDGE", "SUSHU", "WECHAT", "PC_WEB", "MINGYIHUI", "RED_BIRD"};
                Random random = new Random();
                this.source_type = sources[random.nextInt(sources.length)];
            }
        }
    }

    public String transform() {
        return simplify(this).toString();
    }
}
