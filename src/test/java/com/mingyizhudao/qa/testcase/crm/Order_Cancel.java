package com.mingyizhudao.qa.testcase.crm;

import com.mingyizhudao.qa.common.BaseTest;
import org.apache.log4j.Logger;

/**
 * Created by ttshmily on 25/4/2017.
 */
public class Order_Cancel extends BaseTest {
// 待上传小结时，可以取消订单，此时已定金支付完成。
    public static final Logger logger= Logger.getLogger(Order_Cancel.class);
    public static final String version = "/api/v1";
    public static String uri = version+"/orders/{orderNumber}/cancelOrder";
    public static String mock = false ? "/mockjs/1" : "";


}
