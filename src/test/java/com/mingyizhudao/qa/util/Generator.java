package com.mingyizhudao.qa.util;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.KnowledgeBase;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ttshmily on 4/5/2017.
 */
public class Generator {

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(randomEmployeeId());
        }
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

    public static String randomDateFromNow(int i, int j) {
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

    public static String randomDate(String startDate, String endDate) {
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

    public static String randomDateTillNow() {
        try {
            Date start = df.parse("2017/03/13");// 构造开始日期
            Date end = df.parse(df.format(new Date()));// 构造结束日期
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

    public static String randomKey(HashMap<String, String> map) {
        String[] keys = map.keySet().toArray(new String[] {});
        return keys[(int)randomInt(keys.length-1)];
    }

    public static String randomPhone() {
        Random random = new Random();
        Integer m = random.nextInt(99999);
        return "13" + String.format("%05d", m) + "9999";
    }

    public static String randomEmployeeId() {
        String[] prefix = {"SH"};
//        String[] ID = {"0105", "0098", "0130", "0129", "0133"};
        String[] ID = {"0133","0143","9999","0031", "0098","0025"};

        Random random = new Random();
        return prefix[random.nextInt(prefix.length)]+ID[random.nextInt(ID.length)];
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

    public static String countyName(String id) {

        return KnowledgeBase.kb_county.get(id);
    }

    public static String randomHospitalId() {

        return randomKey(KnowledgeBase.kb_hospital);
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

    public static String randomAcademicId() {

        return randomKey(KnowledgeBase.kb_academic_title);
    }

    public static String randomHospitalClass() {

        return randomKey(KnowledgeBase.kb_hospital_class);
    }

    public static String randomHospitalType() {

        return randomKey(KnowledgeBase.kb_hospital_type);
    }

    public static String randomSurgeryId() {

        return randomKey(KnowledgeBase.kb_surgery);
    }

    public static String surgeryName(String id) {

        return KnowledgeBase.kb_surgery.get(id);
    }



    public static String parseJson(JSONObject node, String path) {

        if (node == null) return null;
        if (!path.contains(":")) {
            if ( path.indexOf("(")+1 == path.indexOf(")") ) { // 不指定数组坐标
                if (node.getJSONArray(path.substring(0,path.length()-2)).size() >= 0) { //jsonArray不为空
                    BaseTest.logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.length() - 2)).size());
                    return String.valueOf(node.getJSONArray(path.substring(0,path.length()-2)).size()); //返回数组长度
                } else {
                    return null; //指定的数组key不存在
                }
            } else if ( path.indexOf("(")+1 < path.indexOf(")") ) { // 指定数组坐标
                if (node.getJSONArray(path.substring(0,path.indexOf("("))).size() > 0) {
                    BaseTest.logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.indexOf("("))));
                    return node.getJSONArray(path.substring(0, path.indexOf("("))).getString(Integer.parseInt(path.substring(path.indexOf("(") + 1, path.indexOf(")")))); //返回指定坐标的内容
                } else {
                    return null; // 指定的数组key不存在，或者长度为0
                }
            } else { // 不是数组
                if (node.containsKey(path)) {
                    return node.getString(path); // 返回值,包括""
                } else {
                    return null; // key不存在
                }
            }
        }

        String nextPath = path.substring(path.indexOf(":")+1);
        String head = path.substring(0,path.indexOf(":"));
        if ( head.indexOf("(")+1 == head.indexOf(")") ) {
            if (node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0)
                return parseJson(node.getJSONArray(head.substring(0,head.length()-2)).getJSONObject(0),nextPath);
            else
                return null;
        } else if ( head.indexOf("(")+1 < head.indexOf(")") ) {
            if ( node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0 )
                return parseJson(node.getJSONArray(head.substring(0,path.indexOf("("))).getJSONObject(Integer.parseInt(head.substring(head.indexOf("(")+1,head.indexOf(")")))),nextPath);
            else
                return null;
        } else {
            if (node.containsKey(head)) {
                return parseJson(node.getJSONObject(head), nextPath);
            } else {
                return null;
            }
        }
    }
}
