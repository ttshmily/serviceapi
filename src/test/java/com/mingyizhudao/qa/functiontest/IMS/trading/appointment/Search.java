package com.mingyizhudao.qa.functiontest.IMS.trading.appointment;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;

public class Search extends BaseTest {
    public static String clazzName = new Object() {
        public String getClassName() {
            String clazzName = this.getClass().getName();
            return clazzName.substring(0, clazzName.lastIndexOf('$'));
        }
    }.getClassName();
    public static TestLogger logger = new TestLogger(clazzName);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders";

    public void test_01_按工单号搜索() {

    }

    public void test_02_按预约单号搜索() {

    }

    public void test_03_按患者姓名搜索() {

    }

    public void test_04_按患者手机搜索() {

    }

    public void test_05_按工单提交人搜索() {

    }

    public void test_06_按工单受理人搜索() {

    }

    public void test_07_按提交日期搜索() {

    }
}
