package com.mingyizhudao.qa.testcase.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import com.mingyizhudao.qa.util.UT;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dayi on 2017/6/26.
 */
public class DistrictManagement extends BaseTest {
    public static final Logger logger= Logger.getLogger(DistrictManagement.class);
    public static String uri = "/api/v1/user/districtManagement";
    public static String mock = false ? "/mockjs/1" : "";
    public static String token= "";

    @Test
    public void test_01_给下属BD分配区域(){
        String res = "";
        JSONObject body = new JSONObject();
        String staff_id = UT.randomEmployeeId();
        body.put("staff_id", staff_id);
        List<String> city_list = new ArrayList<>();
        JSONArray cities = new JSONArray();
        for(int i=0; i<2; i++) {
            JSONObject city1 = new JSONObject();
            String city_id = UT.randomCityId();
            city1.put("city_id", city_id);
            city1.put("city_name", UT.cityName(city_id));
            String province_id = UT.randomProvinceId();
            city1.put("province_id", province_id);
            city1.put("province_name", UT.provinceName(province_id));
            cities.add(city1);
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
    }
}
