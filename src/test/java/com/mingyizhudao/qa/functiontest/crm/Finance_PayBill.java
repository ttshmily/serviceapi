package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import org.apache.log4j.Logger;

/**
 * Created by dayi on 2017/7/3.
 */
public class Finance_PayBill extends BaseTest{
    public static final Logger logger= Logger.getLogger(Finance_PayBill.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/finances/pays/{paymentNumber}";
    public static String mock = false ? "/mockjs/1" : "";
}
