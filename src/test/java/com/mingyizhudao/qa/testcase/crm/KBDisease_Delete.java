package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import org.apache.log4j.Logger;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KBDisease_Delete extends BaseTest {

    public static final Logger logger= Logger.getLogger(KBDisease_Delete.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases/{id}";
    public static String mock = false ? "/mockjs/1" : "";

}
