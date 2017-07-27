package com.mingyizhudao.qa.functiontest.crm;

import com.mingyizhudao.qa.common.BaseTest;
import org.apache.log4j.Logger;

/**
 * Created by ttshmily on 1/6/2017.
 */
public class KBDisease_List extends BaseTest {
    public static final Logger logger= Logger.getLogger(KBDisease_List.class);
    public static final String version = "/api/v1";
    public static String uri = version + "/medicallibrary/diseases";
    public static String mock = false ? "/mockjs/1" : "";
}
