package com.mingyizhudao.qa.common;

import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by ttshmily on 28/4/2017.
 */
public class KB {

    public static final Logger logger= Logger.getLogger(KB.class);

    public static String hospital_uri = "/api/v1/hospitals";
    public static HashMap<String, String> kb_hospital = new HashMap<>();
    public static String hospital_file = "";

    public static String doctor_uri = "/api/v1/doctors";
    public static HashMap<String, String> kb_doctor = new HashMap<>();

    public static String province_uri = "/api/v1/provinces";
    public static HashMap<String, String> kb_province = new HashMap<>();

    public static String city_uri = "/api/v1/cities";
    public static HashMap<String, String> kb_city = new HashMap<>();

    public static String medical_uri = "/api/v1/common/medicalTitleList";
    public static HashMap<String, String> kb_medical_title = new HashMap<>();

    public static String academic_uri = "/api/v1/common/academicTitleList";
    public static HashMap<String, String> kb_academic_title = new HashMap<>();

    public static String surgery_category_uri = "/api/v1/surgeryCategories";
    public static HashMap<String, String> kb_surgery_category = new HashMap<>();

    public static String surgery_uri = "/api/v1/surgeries";
    public static HashMap<String, String> kb_surgery = new HashMap<>();

    public static String major_uri = "/diseaseCategories/listTreeNode";
    public static HashMap<String, String> kb_major = new HashMap<>();

    public static String disease_uri = "/api/v1/diseases";
    public static HashMap<String, String> kb_disease = new HashMap<>();

    public static String hospital_type_uri = "/api/v1/common/hospitalTypeList";
    public static HashMap<String, String> kb_hospital_type = new HashMap<>();

    public static String hospital_class_uri = "/api/v1/common/hospitalClassList";
    public static HashMap<String, String> kb_hospital_class = new HashMap<>();

    public static String county_uri = "/api/v1/cities/{id}/counties";
    public static HashMap<String, String> kb_county = new HashMap<>();

    public static void init() {
        try {
            int pageSize = 1000;
            HashMap<String, String> query = new HashMap<>();
            query.put("pageSize", "1");
            query.put("page", "1");
            logger.debug(BaseTest.host_kb);
            String res = HttpRequest.sendGet(BaseTest.host_kb+hospital_uri, query, "", null);
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:size"));
            int num = total / pageSize + 1;
            int last_page_num = total - pageSize*(num-1);

            query.replace("pageSize", String.valueOf(pageSize));
            for (int i = 1; i < num; i++) {
                query.replace("page", String.valueOf(i));
                res = HttpRequest.sendGet(BaseTest.host_kb + hospital_uri, query, "", null);
                JSONArray hospital_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < pageSize; j++) {
                    JSONObject hospital = hospital_list.getJSONObject(j);
                    kb_hospital.put(hospital.getString("id"), hospital.getString("name"));
                }
            }
            query.replace("page", String.valueOf(num));
            res = HttpRequest.sendGet(BaseTest.host_kb +hospital_uri, query, "", null);
            JSONArray hospital_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < last_page_num; j++) {
                JSONObject hospital = hospital_list.getJSONObject(j);
                kb_hospital.put(hospital.getString("id"), hospital.getString("name"));
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(1);
        }

        try {
            int pageSize = 1000;
            HashMap<String, String> query = new HashMap<>();
            query.put("pageSize", "1");
            query.put("page", "1");
            String res = HttpRequest.sendGet(BaseTest.host_kb +doctor_uri, query, "");
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:size"));
            int num = total / pageSize + 1;
            int last_page_num = total - pageSize*(num-1);

            query.replace("pageSize", String.valueOf(pageSize));
            for (int i = 1; i < num; i++) {
                query.replace("page", String.valueOf(i));
                res = HttpRequest.sendGet(BaseTest.host_kb + doctor_uri, query, "");
                JSONArray doctor_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < pageSize; j++) {
                    JSONObject doctor = doctor_list.getJSONObject(j);
                    kb_doctor.put(doctor.getString("id"), doctor.getString("name"));
                }
            }
            query.replace("page", String.valueOf(num));
            res = HttpRequest.sendGet(BaseTest.host_kb +doctor_uri, query, "");
            JSONArray doctor_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < last_page_num; j++) {
                JSONObject doctor = doctor_list.getJSONObject(j);
                kb_doctor.put(doctor.getString("id"), doctor.getString("name"));
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(2);
        }

        try {
            String res = HttpRequest.sendGet(BaseTest.host_kb +province_uri, "", "", null);
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
            JSONArray province_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < total; j++) {
                JSONObject province = province_list.getJSONObject(j);
                kb_province.put(province.getString("id"), province.getString("name"));
            }

        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(3);
        }

        try {
            String res = "";
            HashMap<String, String> query = new HashMap<>();
            query.put("provinceId", "100");
            for (String key:kb_province.keySet()) {
                query.replace("provinceId", key);
                res = HttpRequest.sendGet(BaseTest.host_kb +city_uri, query, "", null);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray city_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject city = city_list.getJSONObject(j);
                    kb_city.put(city.getString("id"), city.getString("name"));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(4);
        }

        try {
            String res = "";
            res = HttpRequest.sendGet(BaseTest.host_kb +medical_uri, "", "", null);
            logger.debug(res);
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
            JSONArray mt_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < total; j++) {
                JSONObject mt = mt_list.getJSONObject(j);
                for (String key:(Set<String>)mt.keySet()) {
                    kb_medical_title.put(key, mt.getString(key));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(5);
        }

        try {
            String res = "";
            res = HttpRequest.sendGet(BaseTest.host_kb +academic_uri, "", "", null);
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
            JSONArray at_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < total; j++) {
                JSONObject at = at_list.getJSONObject(j);
                for (String key:(Set<String>)at.keySet()) {
                    kb_academic_title.put(key, at.getString(key));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(6);
        }

        try {
            String res = "";
            res = HttpRequest.sendGet(BaseTest.host_kb +surgery_category_uri, "", "", null);
            int total_1 = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()")); // 一级分类个数
            JSONArray psc_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < total_1; i++) {
                JSONObject psc = psc_list.getJSONObject(i);
                JSONArray sc_list = psc.getJSONArray("branch");
                for (int j = 0; j < sc_list.size(); j++) {
                    JSONObject sc = sc_list.getJSONObject(j);
                    kb_surgery_category.put(sc.getString("id"), sc.getString("name"));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(7);
        }

        try {
            String res = "";
            HashMap<String, String> query = new HashMap<>();
            query.put("isShowAll", "true");
            query.put("surgeryCategoryId", "100");
            for (String key:kb_surgery_category.keySet()) {
                query.replace("surgeryCategoryId", key);
                res = HttpRequest.sendGet(BaseTest.host_kb +surgery_uri, query, "", null);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray surgery_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < surgery_list.size(); j++) {
                    JSONObject surgery = surgery_list.getJSONObject(j);
                    kb_surgery.put(surgery.getString("id"), surgery.getString("name"));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(8);
        }

        try {
            String res = "";
            res = HttpRequest.sendGet(BaseTest.host_kb + major_uri, "", "", null);
            int total_1 = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()")); // 一级分类个数
            JSONArray pdc_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < total_1; i++) {
                JSONObject pdc = pdc_list.getJSONObject(i);
                JSONArray dc_list = pdc.getJSONArray("branch");
                for (int j = 0; j < dc_list.size(); j++) {
                    JSONObject dc = dc_list.getJSONObject(j);
                    kb_major.put(dc.getString("id"), dc.getString("name"));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(9);
        }

        try {
            String res = "";
            HashMap<String, String> query = new HashMap<>();
            query.put("isShowAll", "true");
            query.put("diseaseCategoryId", "100");
            for (String key:kb_major.keySet()) {
                query.replace("diseaseCategoryId", key);
                res = HttpRequest.sendGet(BaseTest.host_kb +disease_uri, query, "");
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray disease_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < disease_list.size(); j++) {
                    JSONObject disease = disease_list.getJSONObject(j);
                    kb_disease.put(disease.getString("id"), disease.getString("name"));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(10);
        }

        try {
            String res = "";
            res = HttpRequest.sendGet(BaseTest.host_kb + hospital_class_uri, "", "");
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
            JSONArray ct_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < total; j++) {
                JSONObject ct = ct_list.getJSONObject(j);
                for (String key:(Set<String>)ct.keySet()) {
                    kb_hospital_class.put(key, ct.getString(key));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(11);
        }

        try {
            String res = "";
            res = HttpRequest.sendGet(BaseTest.host_kb + hospital_type_uri, "", "");
            int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
            JSONArray tt_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
            for (int j = 0; j < total; j++) {
                JSONObject tt = tt_list.getJSONObject(j);
                for (String key:(Set<String>)tt.keySet()) {
                    kb_hospital_type.put(key, tt.getString(key));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(12);
        }

        try {
            String res = "";
            for ( String cityId: kb_city.keySet()) {
                HashMap<String, String> pathValue = new HashMap<>();
                pathValue.put("id", cityId);
                res = HttpRequest.sendGet(BaseTest.host_kb + county_uri, "", "", pathValue);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray country_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject country = country_list.getJSONObject(j);
                    kb_county.put(country.getString("id"), country.getString("name"));
                }
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(13);
        }
    }


}
