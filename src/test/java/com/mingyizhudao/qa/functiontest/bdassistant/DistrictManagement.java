package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.utilities.HttpRequest;
import com.mingyizhudao.qa.utilities.Generator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dayi on 2017/6/26.
 */
public class DistrictManagement extends BaseTest {
    public static final Logger logger= Logger.getLogger(DistrictManagement.class);
    public static String uri = "/api/v1/user/districtManagement";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    public static void districtManage(String staffId, List<String> cityList) {
        String res = "";
        JSONObject body = new JSONObject();
        JSONArray cities = new JSONArray();
        body.put("staff_id", staffId);
        for(int i=0; i<cityList.size(); i++) {
            String cityId = cityList.get(i);
            JSONObject cityRow = new JSONObject();
            cityRow.put("city_id", cityId);
            cityRow.put("city_name", Generator.cityName(cityId));
            String province_id = Generator.randomProvinceId();
            cityRow.put("province_id", province_id);
            cityRow.put("province_name", Generator.provinceName(province_id));
            cities.add(cityRow);
        }
        body.put("list", cities);
        try {
            res = HttpRequest.sendPost(host_bda + uri, body.toString(), bda_token);
        } catch (IOException e) {
            logger.debug(res);
            logger.error(e);
        }
    }

    @Test
    public void test_01_给下属BD分配区域(){
        String res = "";
        JSONObject body = new JSONObject();
        String staff_id = Generator.randomEmployeeId();
        body.put("staff_id", staff_id);
        List<String> city_list = new ArrayList<>();
        JSONArray cities = new JSONArray();
        for(int i=0; i<2; i++) {
            JSONObject cityRow = new JSONObject();
            String city_id = Generator.randomCityId();
            cityRow.put("city_id", city_id);
            cityRow.put("city_name", Generator.cityName(city_id));
            String province_id = Generator.randomProvinceId();
            cityRow.put("province_id", province_id);
            cityRow.put("province_name", Generator.provinceName(province_id));
            cities.add(cityRow);
            city_list.add(city_id);
        }
        body.put("list", cities);
        try {
            res = HttpRequest.sendPost(host_bda + uri, body.toString(), bda_token);
        } catch (IOException e) {
            logger.error(e);
        }
        checkResponse(res);
        Assert.assertEquals(code, "1000000", "");
        for (String city:city_list
             ) {
            Assert.assertTrue(PersonalInfoV2.BDInfo(bda_token).containsValue(city));
        }
    }
}
