package com.mingyizhudao.qa.utilities;

import com.mingyizhudao.qa.common.KnowledgeBase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ttshmily on 4/5/2017.
 */
public class Generator {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");


    public static void main(String[] args) {
        System.out.println(randomDateFromNow(0, 0, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
        System.out.println(randomDateFromNow(0, 0, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")));
    }

    public static String randomString(int length) {
        StringBuffer sb = new StringBuffer();
        String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt((int)randomInt(len-1)));
        }
        return sb.toString();
    }

    //返回1 - count的int
    public static long randomInt(int count) {
        Random random = new Random();
        return random.nextInt(count)+1;
    }

    public static String randomDateFromNow(int i, int j, SimpleDateFormat df) {
        try {
            Date now = df.parse(df.format(new Date()));
            Calendar date = Calendar.getInstance();
            date.setTime(now);

            date.set(Calendar.DATE, date.get(Calendar.DATE) + i);
            Date start = df.parse(df.format(date.getTime()));// 构造开始日期
            date.set(Calendar.DATE, date.get(Calendar.DATE) + j);
            Date end = df.parse(df.format(date.getTime()));// 构造结束日期

            if (start.getTime() > end.getTime()) {
                return null;
            }
            long mili = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            return df.format(new Date(mili));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomDateFromNow(int i, int j) {
        return randomDateFromNow(i, j, df);
    }

    public static String randomDate(String startDate, String endDate, SimpleDateFormat df) {
        try {
            Date start = df.parse(startDate);// 构造开始日期
            Date end = df.parse(endDate);// 构造结束日期
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = start.getTime() + (long) (Math.random() * (end.getTime() - start.getTime()));
            return df.format(new Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomDate(String startDate, String endDate) {
        return randomDate(startDate, endDate, df);
    }


    public static String randomDateTillNow(SimpleDateFormat df) {
        try {
//            Date start = df.parse("2017/03/13");// 构造开始日期 1489334400
            Date end = df.parse(df.format(new Date()));// 构造结束日期
//            if (start.getTime() >= end.getTime()) {
//                return null;
//            }
            long date = end.getTime() - (long) (Math.random() * (end.getTime() - 1489334400));
            return df.format(new Date(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String randomDateTillNow() {
        return randomDateTillNow(df);
    }

    public static String randomKey(HashMap<String, String> map) {
        String[] keys = map.keySet().toArray(new String[] {});
        if(keys.length==0)
            return "";
        else
            return keys[(int)(randomInt(keys.length)-1)];
    }

    public static String randomPhone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        return "13" + String.format("%05d", m) + "9999";
    }

    public static String randomEmployeeId() {
        String[] prefix = {"SH"};
        String[] ID = {"0133","0143","9999","9998","0025"};
        Random random = new Random();
        return prefix[random.nextInt(prefix.length)]+ID[random.nextInt(ID.length)];
    }

    public static String employeeName(String employeeId) {
        HashMap<String, String> pair = new HashMap<>();
        pair.put("SH0133", "方超");
        pair.put("SH0143", "田静");
        pair.put("SH9998", "田小静");
        pair.put("SH9999", "大一");
        pair.put("SH0025", "牛玉薇");
        return pair.get(employeeId);
    }

    public static String randomProvinceId() {

        return randomKey(KnowledgeBase.kb_province);
    }

    public static String provinceName(String id) {

        return KnowledgeBase.kb_province.get(id);
    }

    public static String randomCityId() {

        return randomKey(KnowledgeBase.kb_city);
    }

    public static String randomCityIdUnder(String provinceId) {

        return randomKey(KnowledgeBase.kb_city_ext.get(provinceId));
    }

    public static String cityName(String id) {

        return KnowledgeBase.kb_city.get(id);
    }

    public static String randomCountyId() {

        return randomKey(KnowledgeBase.kb_county);
    }

    public static String randomCountyIdUnder(String cityId) {

        return randomKey(KnowledgeBase.kb_county_ext.get(cityId));
    }

    public static String countyName(String id) {

        return id.equals("")? "" : KnowledgeBase.kb_county.get(id);
    }

    public static String randomHospitalId() {

        return randomKey(KnowledgeBase.kb_hospital);
    }

    public static String randomHospitalIdWithType(String type) {

        return randomKey(KnowledgeBase.kb_hospital_ext.get(type));
    }

    public static String hospitalName(String id) {

        return KnowledgeBase.kb_hospital.get(id);
    }

    public static String randomMajorId() {

        return randomKey(KnowledgeBase.kb_major);
    }

    public static String majorName(String id) {

        return KnowledgeBase.kb_major.get(id);
    }

    public static String randomDiseaseId() {

        return randomKey(KnowledgeBase.kb_disease);
    }

    public static String randomDiseaseIdUnder(String majorId) {

        return randomKey(KnowledgeBase.kb_disease_ext.get(majorId));
    }


    public static String diseaseName(String id) {

        return KnowledgeBase.kb_disease.get(id);
    }

    public static String randomExpertId() {

        return randomKey(KnowledgeBase.kb_doctor);
    }

    public static String expertName(String id) {

        return KnowledgeBase.kb_doctor.get(id);
    }

    public static String randomMedicalId() {

        return randomKey(KnowledgeBase.kb_medical_title);
    }

    public static String medicalName(String medicalId) {

        return KnowledgeBase.kb_medical_title.get(medicalId);
    }

    public static String randomAcademicId() {

        return randomKey(KnowledgeBase.kb_academic_title);
    }

    public static String academicName(String academiclId) {

        return KnowledgeBase.kb_academic_title.get(academiclId);
    }

    public static String randomHospitalClass() {

        return randomKey(KnowledgeBase.kb_hospital_class);
    }

    public static String randomHospitalType() {

        return randomKey(KnowledgeBase.kb_hospital_type);
    }

    public static String randomDepartmentId() {

        return randomKey(KnowledgeBase.kb_department);
    }

    public static String randomDepartmentIdUnder(String type) {

        return randomKey(KnowledgeBase.kb_department_ext.get(type));
    }

    public static String departmentName(String type) {

        return KnowledgeBase.kb_department.get(type);
    }

    public static String randomSurgeryId() {

        return randomKey(KnowledgeBase.kb_surgery);
    }

    public static String surgeryName(String id) {

        return KnowledgeBase.kb_surgery.get(id);
    }

}
