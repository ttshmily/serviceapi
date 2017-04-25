package com.mingyizhudao.qa.testcase;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Created by ttshmily on 8/4/2017.
 */
public class CrmCertifiedDoctor extends BaseTest {

    public static final Logger logger= Logger.getLogger(CrmCertifiedDoctor.class);

    public static String uri = "/api2/certifieddoctor";
    public static String mock = false ? "/mockjs/1" : "";

    public static boolean certify(String doctorId) {
        String res = "";
        JSONObject certifyBody = new JSONObject();
        certifyBody.accumulate("user_id", doctorId);
        certifyBody.accumulate("is_verified", "1");
        JSONObject body = new JSONObject();
        body.accumulate("doctor", certifyBody);
        try {
            res = HttpRequest.sendPost(host_doc +mock+uri, body.toString(), "");
        } catch (IOException e) {
            logger.error(e);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parseJson(JSONObject.fromObject(res), "code").endsWith("1000000");
    }
}
