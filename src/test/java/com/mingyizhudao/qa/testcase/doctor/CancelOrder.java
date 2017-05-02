package com.mingyizhudao.qa.testcase.doctor;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.util.HttpRequest;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by ttshmily on 2/5/2017.
 */
public class CancelOrder extends BaseTest {
    public static final Logger logger= Logger.getLogger(CancelOrder.class);
    public static String uri = "/api/cancelOrder";
    public static String mock = false ? "/mockjs/1" : "";

    public static String CancelOrder(String token, String orderId) {
        String res = "";
        res = GetOrderDetail.getOrderDetail(token, orderId);
        String status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        if (!(Integer.parseInt(status) < 3000)) {
            logger.error("订单不可取消");
            return status;
        }
        JSONObject body = new JSONObject();
        JSONObject order = new JSONObject();
        order.put("orderNumber", orderId);
        body.put("order", order);

        try {
            res = HttpRequest.sendPost(host_doc + uri, body.toString(), token, null);
        } catch (IOException e) {
            logger.error(e);
        }
        res = GetOrderDetail.getOrderDetail(token, orderId);
        status = JSONObject.fromObject(res).getJSONObject("data").getJSONObject("order").getString("status");
        return status;
    }
}
