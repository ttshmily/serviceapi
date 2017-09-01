package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.utilities.Generator;
import static com.mingyizhudao.qa.utilities.Generator.*;
import com.mingyizhudao.qa.utilities.HttpRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dayi on 2017/6/26.
 */
public class DistrictManagement extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/districtManagement";

    public static void districtManage(String staffId, List<String> cityList) {
        String res = "";
        JSONObject body = new JSONObject();
        JSONArray cities = new JSONArray();
        body.put("staff_id", staffId);
        body.put("staff_name", employeeName(staffId));
        for(int i=0; i<cityList.size(); i++) {
            String cityId = cityList.get(i);
            JSONObject cityRow = new JSONObject();
            String province_id = Generator.randomProvinceId();
            cityRow.put("province_id", province_id);
            cityRow.put("province_name", Generator.provinceName(province_id));
            cityRow.put("city_id", cityId);
            cityRow.put("city_name", Generator.cityName(cityId));
            cities.add(cityRow);
        }
        body.put("city_list", cities);
        res = HttpRequest.s_SendPost(host_bda + uri, body.toString(), bda_session);
    }

    @Test
    public void test_01_给下属BD分配区域(){
        String res = "";
        JSONObject body = new JSONObject();
        String staff_id = Generator.randomEmployeeId();
        body.put("staff_id", staff_id);
        body.put("staff_name", employeeName(staff_id));
        List<String> city_list = new ArrayList<>();
        JSONArray cities = new JSONArray();
        for(int i=0; i<2; i++) {
            JSONObject cityRow = new JSONObject();
            String province_id = Generator.randomProvinceId();
            cityRow.put("province_id", province_id);
            cityRow.put("province_name", Generator.provinceName(province_id));
            String city_id = Generator.randomCityIdUnder(province_id);
            cityRow.put("city_id", city_id);
            cityRow.put("city_name", Generator.cityName(city_id));
            cities.add(cityRow);
            city_list.add(city_id);
        }
        body.put("city_list", cities);
        res = HttpRequest.s_SendPost(host_bda + uri, body.toString(), bda_session);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000", "");
        for (String city:city_list) {
            Assert.assertTrue(PersonalInfoV2.BDInfo(bda_session).containsValue(city));
        }
    }
}
