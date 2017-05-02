package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.testcase.crm.Order_Detail;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class PaymentResult extends BaseTest {
    public static final Logger logger = Logger.getLogger(CreateSurgeryBriefs.class);
    public static String uri = "/api/paymentResult";
    public static String mock = false ? "/mockjs/1" : "";

    public static String result(String orderId, String token) {

        String res = "";
        res = GetOrderDetail.getOrderDetail(token, orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);
        HashMap<String, String> pathValue = new HashMap<>();
        pathValue.put("orderId", orderId);
        try {
            res = HttpRequest.sendPost(host_doc + uri, body.toString(), token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        String ispaid = JSONObject.fromObject(res).getJSONObject("data").getString("ispaid");
        return ispaid.equals("true") ? "4000" : status;
    }
}

