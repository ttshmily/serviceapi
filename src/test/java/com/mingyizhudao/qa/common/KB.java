package com.mingyizhudao.qa.common;

import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.omg.CORBA.DATA_CONVERSION;

import java.io.*;
import java.util.*;

//created by tianjing on 2017/6/21


/**
 * Created by ttshmily on 28/4/2017.
 */
public class KB {

    public static final Logger logger= Logger.getLogger(KB.class);

    public static String hospital_uri = "/api/v1/hospitals";
    public static HashMap<String, String> kb_hospital = new HashMap<>();
    public static String hospital_file = "src/test/resources/kb_hospital.txt";

    public static String doctor_uri = "/api/v1/doctors";
    public static HashMap<String, String> kb_doctor = new HashMap<>();
    public static String doctor_file = "src/test/resources/kb_doctor.txt";

    public static String province_uri = "/api/v1/provinces";
    public static HashMap<String, String> kb_province = new HashMap<>();
    public static String province_file = "src/test/resources/kb_province.txt";

    public static String city_uri = "/api/v1/cities";
    public static HashMap<String, String> kb_city = new HashMap<>();
    public static String city_file = "src/test/resources/kb_city.txt";

    public static String medical_uri = "/api/v1/common/medicalTitleList";
    public static HashMap<String, String> kb_medical_title = new HashMap<>();
    public static String medical_title_file = "src/test/resources/kb_medical_title.txt";

    public static String academic_uri = "/api/v1/common/academicTitleList";
    public static HashMap<String, String> kb_academic_title = new HashMap<>();
    public static String academic_title_file = "src/test/resources/kb_academic_title.txt";

    public static String surgery_category_uri = "/api/v1/surgeryCategories";
    public static HashMap<String, String> kb_surgery_category = new HashMap<>();
    public static String surgery_category_file = "src/test/resources/kb_surgery_category.txt";

    public static String surgery_uri = "/api/v1/surgeries";
    public static HashMap<String, String> kb_surgery = new HashMap<>();
    public static String surgery_file = "src/test/resources/kb_surgery.txt";

    public static String major_uri = "/diseaseCategories/listTreeNode";
    public static HashMap<String, String> kb_major = new HashMap<>();
    public static String major_file = "src/test/resources/kb_major.txt";

    public static String disease_uri = "/api/v1/diseases";
    public static HashMap<String, String> kb_disease = new HashMap<>();
    public static String disease_file = "src/test/resources/kb_disease.txt";

    public static String hospital_type_uri = "/api/v1/common/hospitalTypeList";
    public static HashMap<String, String> kb_hospital_type = new HashMap<>();
    public static String hospital_type_file = "src/test/resources/kb_hospital_type.txt";

    public static String hospital_class_uri = "/api/v1/common/hospitalClassList";
    public static HashMap<String, String> kb_hospital_class = new HashMap<>();
    public static String hospital_class_file = "src/test/resources/kb_hospital_class.txt";

    public static String county_uri = "/api/v1/cities/{id}/counties";
    public static HashMap<String, String> kb_county = new HashMap<>();
    public static String county_file = "src/test/resources/kb_county.txt";

    public static void init() {
        try {
            File file = new File(hospital_file);
            if (file.exists()) {
                fileToString(hospital_file,kb_hospital);
            }else {
                int pageSize = 1000;
                HashMap<String, String> query = new HashMap<>();
                query.put("pageSize", "1");
                query.put("page", "1");
                logger.debug(BaseTest.host_kb);
                String res = HttpRequest.sendGet(BaseTest.host_kb + hospital_uri, query, "", null);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:size"));
                int num = total / pageSize + 1;
                int last_page_num = total - pageSize * (num - 1);

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
                res = HttpRequest.sendGet(BaseTest.host_kb + hospital_uri, query, "", null);
                JSONArray hospital_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < last_page_num; j++) {
                    JSONObject hospital = hospital_list.getJSONObject(j);
                    kb_hospital.put(hospital.getString("id"), hospital.getString("name"));
                }
                stringToFile(kb_hospital,"src/test/resources/kb_hospital.txt");//created by tianjing on 2017/6/21
            }
            //writeJson("/src/test/resources/",)
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(1);
        }

        try {
            File file = new File(doctor_file);
            if (file.exists()) {
                fileToString(doctor_file,kb_doctor);
            }else {
                int pageSize = 1000;
                HashMap<String, String> query = new HashMap<>();
                query.put("pageSize", "1");
                query.put("page", "1");
                String res = HttpRequest.sendGet(BaseTest.host_kb + doctor_uri, query, "");
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:size"));
                int num = total / pageSize + 1;
                int last_page_num = total - pageSize * (num - 1);

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
                res = HttpRequest.sendGet(BaseTest.host_kb + doctor_uri, query, "");
                JSONArray doctor_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < last_page_num; j++) {
                    JSONObject doctor = doctor_list.getJSONObject(j);
                    kb_doctor.put(doctor.getString("id"), doctor.getString("name"));
                }
                stringToFile(kb_doctor, "src/test/resources/kb_doctor.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(2);
        }

        try {
            File file = new File(province_file);
            if (file.exists()) {
                fileToString(province_file,kb_province);
            }else {
                String res = HttpRequest.sendGet(BaseTest.host_kb + province_uri, "", "", null);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray province_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject province = province_list.getJSONObject(j);
                    kb_province.put(province.getString("id"), province.getString("name"));
                }
                stringToFile(kb_province, "src/test/resources/kb_province.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(3);
        }

        try {
            File file = new File(city_file);
            if (file.exists()) {
                fileToString(city_file,kb_city);
            }else {
                String res = "";
                HashMap<String, String> query = new HashMap<>();
                query.put("provinceId", "100");
                for (String key : kb_province.keySet()) {
                    query.replace("provinceId", key);
                    res = HttpRequest.sendGet(BaseTest.host_kb + city_uri, query, "", null);
                    int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                    JSONArray city_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                    for (int j = 0; j < total; j++) {
                        JSONObject city = city_list.getJSONObject(j);
                        kb_city.put(city.getString("id"), city.getString("name"));
                    }
                }
                stringToFile(kb_city, "src/test/resources/kb_city.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(4);
        }

        try {
            File file = new File(medical_title_file);
            if (file.exists()) {
                fileToString(medical_title_file,kb_medical_title);
            }else {
                String res = "";
                res = HttpRequest.sendGet(BaseTest.host_kb + medical_uri, "", "", null);
                logger.debug(res);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray mt_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject mt = mt_list.getJSONObject(j);
                    for (String key : (Set<String>) mt.keySet()) {
                        kb_medical_title.put(key, mt.getString(key));
                    }
                }
                stringToFile(kb_medical_title, "src/test/resources/kb_medical_title.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(5);
        }

        try {
            File file = new File(academic_title_file);
            if (file.exists()) {
                fileToString(academic_title_file,kb_academic_title);
            }else {
                String res = "";
                res = HttpRequest.sendGet(BaseTest.host_kb + academic_uri, "", "", null);
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray at_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject at = at_list.getJSONObject(j);
                    for (String key : (Set<String>) at.keySet()) {
                        kb_academic_title.put(key, at.getString(key));
                    }
                }
                stringToFile(kb_academic_title, "src/test/resources/kb_academic_title.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(6);
        }

        try {
            File file = new File(surgery_category_file);
            if (file.exists()) {
                fileToString(surgery_category_file,kb_surgery_category);
            }else {
                String res = "";
                res = HttpRequest.sendGet(BaseTest.host_kb + surgery_category_uri, "", "", null);
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
                stringToFile(kb_surgery_category, "src/test/resources/kb_surgery_category.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(7);
        }

        try {
            File file = new File(surgery_file);
            if (file.exists()) {
                fileToString(surgery_file,kb_surgery);
            }else {
                String res = "";
                HashMap<String, String> query = new HashMap<>();
                query.put("isShowAll", "true");
                query.put("surgeryCategoryId", "100");
                for (String key : kb_surgery_category.keySet()) {
                    query.replace("surgeryCategoryId", key);
                    res = HttpRequest.sendGet(BaseTest.host_kb + surgery_uri, query, "", null);
                    int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                    JSONArray surgery_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                    for (int j = 0; j < surgery_list.size(); j++) {
                        JSONObject surgery = surgery_list.getJSONObject(j);
                        kb_surgery.put(surgery.getString("id"), surgery.getString("name"));
                    }
                }
                stringToFile(kb_surgery, "src/test/resources/kb_surgery.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(8);
        }

        try {
            File file = new File(major_file);
            if (file.exists()) {
                fileToString(major_file,kb_major);
            }else {
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
                stringToFile(kb_major, "src/test/resources/kb_major.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(9);
        }

        try {
            File file = new File(disease_file);
            if (file.exists()) {
                fileToString(disease_file,kb_disease);
            }else {
                String res = "";
                HashMap<String, String> query = new HashMap<>();
                query.put("isShowAll", "true");
                query.put("diseaseCategoryId", "100");
                for (String key : kb_major.keySet()) {
                    query.replace("diseaseCategoryId", key);
                    res = HttpRequest.sendGet(BaseTest.host_kb + disease_uri, query, "");
                    int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                    JSONArray disease_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                    for (int j = 0; j < disease_list.size(); j++) {
                        JSONObject disease = disease_list.getJSONObject(j);
                        kb_disease.put(disease.getString("id"), disease.getString("name"));
                    }
                }
                stringToFile(kb_disease, "src/test/resources/kb_disease.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            System.exit(10);
        }

        try {
            File file = new File(hospital_class_file);
            if (file.exists()) {
                fileToString(hospital_class_file,kb_hospital_class);
            }else {
                String res = "";
                res = HttpRequest.sendGet(BaseTest.host_kb + hospital_class_uri, "", "");
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray ct_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject ct = ct_list.getJSONObject(j);
                    for (String key : (Set<String>) ct.keySet()) {
                        kb_hospital_class.put(key, ct.getString(key));
                    }
                }
                stringToFile(kb_hospital_class, "src/test/resources/kb_hospital_class.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(11);
        }

        try {
            File file = new File(hospital_type_file);
            if (file.exists()) {
                fileToString(hospital_type_file,kb_hospital_type);
            }else {
                String res = "";
                res = HttpRequest.sendGet(BaseTest.host_kb + hospital_type_uri, "", "");
                int total = Integer.parseInt(BaseTest.parseJson(JSONObject.fromObject(res), "data:list()"));
                JSONArray tt_list = JSONObject.fromObject(res).getJSONObject("data").getJSONArray("list");
                for (int j = 0; j < total; j++) {
                    JSONObject tt = tt_list.getJSONObject(j);
                    for (String key : (Set<String>) tt.keySet()) {
                        kb_hospital_type.put(key, tt.getString(key));
                    }
                }
                stringToFile(kb_hospital_type, "src/test/resources/kb_hospital_type.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(12);
        }

        try {
            File file = new File(county_file);
            if (file.exists()) {
                fileToString(county_file,kb_county);
            }else {
                String res = "";
                for (String cityId : kb_city.keySet()) {
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
                stringToFile(kb_county, "src/test/resources/kb_county.txt");//created by tianjing on 2017/6/21
            }
        } catch (Exception e) {
            logger.error("ENUM初始化失败，准备退出");
            logger.error(e);
            System.exit(13);
        }
    }

    //created by tianjing on 2017/6/21
    private static void stringToFile(HashMap<String,String> id_name,String filePath){
        try{
            StringBuffer id_name_buffer = new StringBuffer();
            for (Map.Entry<String,String> id_name_list : id_name.entrySet()){
                id_name_buffer.append(id_name_list.getKey()).append(",").append(id_name_list.getValue()).append(";").append("\r\n");
            }

            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(id_name_buffer.toString());
            fileWriter.close();
        }catch (IOException e){
            logger.error(e);
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) throws IOException {
        HashMap id_name = new HashMap();
        id_name.put("1","第一行");
        id_name.put("2","第二行");
        hashMapToFile(id_name,"src/test/resources/test.txt");
    }*/

    public  static void fileToString(String filePath,HashMap<String,String> id_name){
        try {
            String encoding="UTF-8";
            String  lineTxt = null;
            File file = new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                       new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                while((lineTxt = bufferedReader.readLine()) != null){
                    String[] s = lineTxt.split(";");
                    for (String s1:s){
                        String[] ms = s1.split(",");
                        id_name.put(ms[0],ms[1]);
                    }
                }
                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

   /* public static void main(String[] args) throws IOException{
        fileToString("src/test/resources/kb_hospital.txt",kb_hospital);
    }*/
}
