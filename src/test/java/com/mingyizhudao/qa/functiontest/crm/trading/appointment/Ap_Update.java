package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import com.mingyizhudao.qa.dataprofile.crm.Appointment;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.mingyizhudao.qa.utilities.HttpRequest.*;
import static com.mingyizhudao.qa.utilities.Helper.*;

import java.util.*;

public class Ap_Update extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/appointments/{orderNumber}";

    @Test
    public void test_01_更新会诊单图片() {
        String res = "";
        Appointment ap = new Appointment();
        String orderNumber = Ap_Create.s_Create(ap);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderNumber", orderNumber);
        ap.setMedical_record_pictures(new ArrayList<Appointment.Picture>() {
            {
                add(ap.new Picture("234.jpg", "7"));
            }
        });
        res = s_SendPut(host_appointment + uri, "", crm_token, pathValue);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        res = Ap_Detail.s_Detail(orderNumber);
        s_CheckResponse(res);
        Assert.assertEquals(code, "1000000");
        Assert.assertEquals(s_ParseJson(data, "medical_record_pictures"), ap.printPictures());

    }

    public void test_02_更新期望手术医院() {

    }

    public void test_03_更新期望手术城市() {

    }

    public void test_04_更新期望手术医生() {

    }

    public void test_05_更新疾病名称() {

    }

    public void test_06_更新患者姓名() {

    }

    public void test_07_更新患者() {

    }

}
