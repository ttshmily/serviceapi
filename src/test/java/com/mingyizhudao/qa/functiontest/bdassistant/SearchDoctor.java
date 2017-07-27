package com.mingyizhudao.qa.functiontest.bdassistant;

import com.mingyizhudao.qa.common.BaseTest;
import org.apache.log4j.Logger;

/**
 * Created by ttshmily on 17/5/2017.
 */
public class SearchDoctor extends BaseTest {

    public static final Logger logger= Logger.getLogger(SearchDoctor.class);
    public static String uri = "/api/v1/doctors/search";
    public static String mock = false ? "/mockjs/1" : "";

}
