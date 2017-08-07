package com.mingyizhudao.qa.functiontest.crm.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;

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

    public void test_01_更新会诊单图片() {

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
